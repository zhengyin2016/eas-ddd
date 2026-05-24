# CRM上下文 - 服务驱动设计

## 业务服务识别

通过分析CRM业务流程，识别出以下10个核心业务服务：

1. 创建客户
2. 更新客户
3. 创建商机
4. 推进商机阶段
5. 创建合同
6. 审核合同
7. 制定回款计划
8. 记录回款
9. 查询客户信息
10. 分析销售漏斗

## 服务规约

### 1. 创建客户 CreateCustomer

**业务描述**：创建新的客户记录，初始化客户等级为D级，添加主要联系人。

**输入**：
- name: String（客户名称，必填）
- industry: String（所属行业）
- source: CustomerSource（客户来源）
- contactName: String（联系人姓名，必填）
- contactPhone: String（联系人电话，必填）
- contactEmail: String（联系人邮箱）
- address: String（客户地址）
- creatorId: String（创建人ID，必填）

**输出**：
- customerId: CustomerId

**业务规则**：
1. 客户名称不能为空
2. 至少需要一个联系人
3. 新客户默认等级为D级
4. 联系人自动设为主要联系人
5. 需验证联系人电话格式

**前置条件**：
- 客户名称未被占用（同一客户名称下只能有一个活跃客户）

**后置条件**：
- 客户记录已保存
- 客户等级为D级
- 联系人已创建且为主要联系人

**异常处理**：
- CustomerAlreadyExistsException：客户已存在
- InvalidContactException：联系人信息无效

---

### 2. 更新客户 UpdateCustomer

**业务描述**：更新客户基本信息，不包括客户等级（等级由系统自动评估）。

**输入**：
- customerId: CustomerId
- name: String
- industry: String
- address: String

**输出**：
- CustomerResponse（更新后的客户信息）

**业务规则**：
1. 只允许更新名称、行业、地址
2. 客户等级由系统自动维护，不允许手动更新
3. 客户来源创建后不允许修改

**前置条件**：
- 客户存在

**后置条件**：
- 客户信息已更新

**异常处理**：
- CustomerNotFoundException：客户不存在

---

### 3. 创建商机 CreateOpportunity

**业务描述**：为客户创建新的销售商机。

**输入**：
- customerId: CustomerId
- title: String（商机标题，必填）
- estimatedAmount: Money（预计金额，必填）
- expectedCloseDate: LocalDate（预计成交日期）
- ownerId: String（负责人ID，必填）

**输出**：
- opportunityId: OpportunityId

**业务规则**：
1. 初始阶段为INITIAL_CONTACT
2. 初始赢率为20%
3. 预计成交日期不能早于今天
4. 预计金额必须大于0

**前置条件**：
- 客户存在

**后置条件**：
- 商机记录已创建
- 阶段为INITIAL_CONTACT
- 赢率为20%

**异常处理**：
- CustomerNotFoundException：客户不存在
- InvalidAmountException：金额无效

---

### 4. 推进商机阶段 AdvanceOpportunityStage

**业务描述**：将商机推进到下一个阶段，自动更新赢率。

**输入**：
- opportunityId: OpportunityId
- newStage: OpportunityStage

**输出**：
- OpportunityResponse（更新后的商机信息）

**业务规则**：
1. 阶段只能向前推进，不能回退
2. 赢率根据阶段自动更新
3. 从CONTRACT_SIGNING推进完成时自动标记为赢单

**前置条件**：
- 商机存在
- 商机未关闭（未赢单/输单）
- 新阶段在当前阶段之后

**后置条件**：
- 商机阶段已更新
- 赢率已更新
- 若到达CONTRACT_SIGNING，自动赢单

**异常处理**：
- OpportunityNotFoundException：商机不存在
- InvalidStageTransitionException：阶段转换无效

---

### 5. 创建合同 CreateContract

**业务描述**：基于商机创建销售合同。

**输入**：
- customerId: CustomerId
- opportunityId: OpportunityId
- title: String（合同标题，必填）
- amount: Money（合同金额，必填）
- signDate: LocalDate（签约日期）
- startDate: LocalDate（生效日期）
- endDate: LocalDate（终止日期）

**输出**：
- contractId: ContractId

**业务规则**：
1. 初始状态为DRAFT
2. 合同金额必须大于0
3. 开始日期必须早于结束日期
4. 关联的商机必须存在
5. 关联商机自动标记为赢单

**前置条件**：
- 客户存在
- 商机存在且未关闭

**后置条件**：
- 合同记录已创建
- 合同状态为DRAFT
- 关联商机标记为赢单

**异常处理**：
- CustomerNotFoundException：客户不存在
- OpportunityNotFoundException：商机不存在
- InvalidContractException：合同日期无效

---

### 6. 审核合同 ReviewContract

**业务描述**：审核合同，通过或拒绝。

**输入**：
- contractId: ContractId
- approve: boolean（是否通过）
- reason: String（拒绝原因，拒绝时必填）
- reviewerId: String（审核人ID）

**输出**：
- ContractResponse（更新后的合同信息）

**业务规则**：
1. 只有DRAFT和UNDER_REVIEW状态的合同可以审核
2. 审核通过：状态变为APPROVED
3. 审核拒绝：状态变为DRAFT
4. 拒绝时必须提供原因
5. 审核通过后可以激活

**前置条件**：
- 合同存在
- 合同状态为DRAFT或UNDER_REVIEW

**后置条件**：
- 合同状态已更新
- 审核记录已保存

**异常处理**：
- ContractNotFoundException：合同不存在
- InvalidContractStatusException：合同状态不允许审核

---

### 7. 制定回款计划 CreatePaymentPlan

**业务描述**：为合同添加回款计划。

**输入**：
- contractId: ContractId
- plans: List<PaymentPlanRequest>（回款计划列表）
  - amount: Money
  - dueDate: LocalDate

**输出**：
- List<PaymentPlanId>

**业务规则**：
1. 回款计划总金额必须等于合同金额
2. 每笔回款金额必须大于0
3. 回款日期必须晚于合同开始日期
4. 只有DRAFT状态的合同可以添加回款计划

**前置条件**：
- 合同存在
- 合同状态为DRAFT
- 原有回款计划（如有）将被替换

**后置条件**：
- 回款计划已创建
- 回款计划关联到合同

**异常处理**：
- ContractNotFoundException：合同不存在
- InvalidPaymentPlanException：回款计划总额不匹配

---

### 8. 记录回款 RecordPayment

**业务描述**：记录合同的实际回款。

**输入**：
- contractId: ContractId
- amount: Money（回款金额，必填）
- paymentDate: LocalDate（回款日期，必填）
- paymentMethod: PaymentMethod（支付方式）
- remark: String（备注）

**输出**：
- paymentId: PaymentId

**业务规则**：
1. 回款金额必须大于0
2. 回款日期不能晚于今天
3. 回款确认后更新合同的已回款金额
4. 全部回款完成后合同状态自动变为COMPLETED
5. 回款确认后发布PaymentConfirmedEvent

**前置条件**：
- 合同存在且状态为ACTIVE

**后置条件**：
- 回款记录已创建
- 回款状态为PLANNED（需要后续确认）
- 回款确认后触发客户等级重新评估

**异常处理**：
- ContractNotFoundException：合同不存在
- InvalidContractStatusException：合同状态不允许回款
- InvalidAmountException：金额无效

---

### 9. 查询客户信息 GetCustomer

**业务描述**：查询客户详细信息，包括联系人和商机。

**输入**：
- customerId: CustomerId

**输出**：
- CustomerResponse（客户详细信息）
  - 基本信息
  - 联系人列表
  - 相关商机列表
  - 相关合同列表

**业务规则**：
1. 返回完整的客户信息
2. 包含所有联系人（标记主要联系人）
3. 包含所有活跃商机
4. 包含所有合同

**前置条件**：
- 客户存在

**后置条件**：
- 无（只读操作）

**异常处理**：
- CustomerNotFoundException：客户不存在

---

### 10. 分析销售漏斗 AnalyzeSalesFunnel

**业务描述**：分析销售漏斗数据，统计各阶段商机情况。

**输入**：
- startDate: LocalDate（统计开始日期）
- endDate: LocalDate（统计结束日期）
- ownerId: String（负责人ID，可选）

**输出**：
- SalesFunnelResponse
  - 各阶段商机数量
  - 各阶段商机金额
  - 转化率
  - 平均成交周期

**业务规则**：
1. 统计指定时间范围内创建的商机
2. 按阶段分组统计
3. 计算相邻阶段转化率
4. 计算平均成交周期（从创建到赢单）

**前置条件**：
- 开始日期早于结束日期

**后置条件**：
- 无（只读操作）

**异常处理**：
- InvalidDateRangeException：日期范围无效

---

## 应用服务接口

```java
public interface CustomerAppService {
    CustomerResponse createCustomer(CreateCustomerRequest request);
    CustomerResponse updateCustomer(UpdateCustomerRequest request);
    CustomerResponse getCustomer(CustomerId customerId);
}

public interface OpportunityAppService {
    OpportunityResponse createOpportunity(CreateOpportunityRequest request);
    OpportunityResponse advanceStage(AdvanceStageRequest request);
    List<OpportunityResponse> findByCustomerId(CustomerId customerId);
}

public interface ContractAppService {
    ContractResponse createContract(CreateContractRequest request);
    ContractResponse reviewContract(ReviewContractRequest request);
    ContractResponse getContract(ContractId contractId);
    void createPaymentPlan(CreatePaymentPlanRequest request);
}

public interface PaymentAppService {
    PaymentResponse recordPayment(RecordPaymentRequest request);
    List<PaymentResponse> findByContractId(ContractId contractId);
}

public interface SalesAnalysisAppService {
    SalesFunnelResponse analyzeSalesFunnel(AnalyzeSalesFunnelRequest request);
}
```

## ACL（防腐层）设计

### PM上下文集成

```java
// 端口：通知PM上下文创建项目
public interface PMClientPort {
    void createProject(CreateProjectCommand command);
}

// 适配器实现
@Component
public class PMClientAdapter implements PMClientPort {
    private final PMApiClient apiClient;

    @Override
    public void createProject(CreateProjectCommand command) {
        // 调用PM上下文REST API
        apiClient.createProject(mapToRequest(command));
    }
}
```

### 财务系统集成

```java
// 端口：发送回款数据到财务系统
public interface FinanceClientPort {
    void sendPaymentData(PaymentData paymentData);
}

// 适配器实现
@Component
public class FinanceClientAdapter implements FinanceClientPort {
    private final FinanceApiClient apiClient;

    @Override
    public void sendPaymentData(PaymentData paymentData) {
        // 发送到财务系统
        apiClient.recordPayment(mapToRequest(paymentData));
    }
}
```
