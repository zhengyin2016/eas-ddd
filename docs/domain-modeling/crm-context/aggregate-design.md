# CRM上下文 - 聚合设计

## 设计原则

1. **聚合边界清晰**：每个聚合有明确的聚合根，外部只能通过聚合根访问内部对象
2. **聚合内强一致性**：聚合内部的所有修改通过聚合根进行，保证一致性
3. **聚合间最终一致性**：聚合之间的交互通过领域事件实现最终一致性
4. **聚合小而美**：聚合设计尽可能小，避免大聚合

## Customer 聚合

### 聚合根：Customer

```java
public class Customer {
    private CustomerId id;
    private String name;
    private String industry;
    private CustomerLevel level;
    private CustomerSource source;
    private String contactName;
    private String contactPhone;
    private String address;
    private CreatorId creatorId;
    private LocalDateTime createdAt;
    private List<Contact> contacts;

    // 行为
    public void update(String name, String industry, String address);
    public void addContact(Contact contact);
    public void updateContact(ContactId contactId, String name, String phone, String email);
    public void setPrimaryContact(ContactId contactId);
    public void upgradeLevel();
    public void downgradeLevel();

    // 领域规则验证
    private void validateContactLimit();
    private void ensurePrimaryContactExists();
}
```

### 实体：Contact

```java
public class Contact {
    private ContactId id;
    private String name;
    private String phone;
    private String email;
    private String position;
    private boolean isPrimary;

    // 行为
    public void update(String name, String phone, String email, String position);
    public void setPrimary();
    public void unsetPrimary();

    // 领域规则
    private void validatePhoneNumber();
    private void validateEmail();
}
```

### 值对象：CustomerLevel

```java
public enum CustomerLevel {
    A("A级", "VIP客户，累计交易额≥100万或年交易≥10次"),
    B("B级", "优质客户，累计交易额≥50万或年交易≥5次"),
    C("C级", "普通客户，累计交易额≥10万或年交易≥1次"),
    D("D级", "潜力客户，新客户");

    private final String code;
    private final String description;
}
```

### 值对象：CustomerSource

```java
public enum CustomerSource {
    REFERRAL("推荐", "客户推荐"),
    EXHIBITION("展会", "展会获取"),
    WEBSITE("网站", "网站咨询"),
    COLD_CALL("电销", "电话销售"),
    OTHER("其他", "其他渠道");

    private final String code;
    private final String description;
}
```

### 聚合不变量

1. 客户至少有一个联系人
2. 有且只能有一个主要联系人（isPrimary=true）
3. 客户名称不能为空
4. 联系人电话格式必须有效
5. 联系人邮箱格式必须有效

## Opportunity 聚合

### 聚合根：Opportunity

```java
public class Opportunity {
    private OpportunityId id;
    private CustomerId customerId;
    private String title;
    private Money estimatedAmount;
    private OpportunityStage stage;
    private int probability;
    private LocalDate expectedCloseDate;
    private OwnerId ownerId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // 行为
    public void advanceStage(OpportunityStage newStage);
    public void markWon(Money actualAmount);
    public void markLost(String reason);
    public void updateExpectedCloseDate(LocalDate newDate);

    // 领域规则验证
    private void validateStageTransition(OpportunityStage newStage);
    private void updateProbability();
}
```

### 值对象：OpportunityStage

```java
public enum OpportunityStage {
    INITIAL_CONTACT("初步接触", 20),
    NEEDS_CONFIRMATION("需求确认", 40),
    PROPOSAL("方案报价", 60),
    NEGOTIATION("商务谈判", 80),
    CONTRACT_SIGNING("合同签订", 95);

    private final String description;
    private final int probability; // 赢率百分比
}
```

### 聚合不变量

1. 商机阶段只能向前推进
2. 赢率必须与阶段匹配
3. 预计成交日期不能早于今天
4. 赢单/输单后不能再修改阶段

## Contract 聚合

### 聚合根：Contract

```java
public class Contract {
    private ContractId id;
    private CustomerId customerId;
    private OpportunityId opportunityId;
    private String title;
    private Money amount;
    private ContractStatus status;
    private LocalDate signDate;
    private LocalDate startDate;
    private LocalDate endDate;
    private Money paidAmount;
    private List<PaymentPlan> paymentPlans;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // 行为
    public void submitForReview();
    public void approve(String approverId);
    public void reject(String reason);
    public void activate();
    public void complete();
    public void terminate(String reason);
    public void addPaymentPlan(PaymentPlan plan);
    public void updatePaidAmount(Money amount);

    // 领域规则验证
    private void validateStatusTransition(ContractStatus newStatus);
    private void validatePaymentPlans();
}
```

### 实体：PaymentPlan

```java
public class PaymentPlan {
    private PaymentPlanId id;
    private ContractId contractId;
    private Money amount;
    private LocalDate dueDate;
    private PaymentPlanStatus status;
    private LocalDateTime createdAt;

    // 行为
    public void markPaid();
    public void markOverdue();

    // 领域规则
    private void validateDueDate();
}
```

### 值对象：ContractStatus

```java
public enum ContractStatus {
    DRAFT("草稿"),
    UNDER_REVIEW("审核中"),
    APPROVED("已审批"),
    ACTIVE("执行中"),
    COMPLETED("已完成"),
    TERMINATED("已终止");

    private final String description;
}
```

### 聚合不变量

1. 合同金额必须大于0
2. 合同开始日期必须早于结束日期
3. 回款计划总金额必须等于合同金额
4. 合同状态流转必须符合业务规则
5. 已回款金额不能超过合同金额

## Payment 聚合

### 聚合根：Payment

```java
public class Payment {
    private PaymentId id;
    private ContractId contractId;
    private Money amount;
    private LocalDate paymentDate;
    private PaymentMethod paymentMethod;
    private PaymentStatus status;
    private String remark;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // 行为
    public void confirm();
    public void markOverdue();
    public void cancel(String reason);

    // 领域事件
    public PaymentConfirmedEvent toConfirmedEvent();

    // 领域规则验证
    private void validatePaymentDate();
}
```

### 值对象：PaymentStatus

```java
public enum PaymentStatus {
    PLANNED("计划中"),
    PAID("已支付"),
    OVERDUE("逾期"),
    CANCELLED("已取消");

    private final String description;
}
```

### 值对象：PaymentMethod

```java
public enum PaymentMethod {
    BANK_TRANSFER("银行转账"),
    CHECK("支票"),
    CASH("现金"),
    ELECTRONIC("电子支付");

    private final String description;
}
```

### 聚合不变量

1. 回款金额必须大于0
2. 回款日期不能晚于今天
3. 回款确认后不能修改

## 聚合间协作

### Contract → Opportunity

```java
// Contract包含OpportunityId引用，但不是直接依赖
public class Contract {
    private OpportunityId opportunityId; // 仅存储ID，不引用Opportunity聚合
}
```

### Payment → Contract

```java
// Payment包含ContractId引用
public class Payment {
    private ContractId contractId;
}

// Payment确认后发布事件，Contract监听并更新已回款金额
public PaymentConfirmedEvent toConfirmedEvent() {
    return new PaymentConfirmedEvent(
        this.id,
        this.contractId,
        this.amount,
        this.paymentDate
    );
}
```

## 领域事件设计

| 事件 | 属性 | 发布时机 |
|------|------|---------|
| ContractSignedEvent | contractId, customerId, amount, signDate | Contract状态变为ACTIVE |
| PaymentConfirmedEvent | paymentId, contractId, amount, paymentDate | Payment状态变为PAID |
| OpportunityWonEvent | opportunityId, customerId, actualAmount | Opportunity调用markWon() |
| OpportunityLostEvent | opportunityId, customerId, reason | Opportunity调用markLost() |
| CustomerLevelChangedEvent | customerId, oldLevel, newLevel | Customer等级变更 |

## 资源库接口

```java
public interface CustomerRepository {
    Customer save(Customer customer);
    Optional<Customer> findById(CustomerId id);
    List<Customer> findByLevel(CustomerLevel level);
    void delete(CustomerId id);
}

public interface OpportunityRepository {
    Opportunity save(Opportunity opportunity);
    Optional<Opportunity> findById(OpportunityId id);
    List<Opportunity> findByCustomerId(CustomerId customerId);
    List<Opportunity> findByStage(OpportunityStage stage);
}

public interface ContractRepository {
    Contract save(Contract contract);
    Optional<Contract> findById(ContractId id);
    List<Contract> findByCustomerId(CustomerId customerId);
}

public interface PaymentRepository {
    Payment save(Payment payment);
    Optional<Payment> findById(PaymentId id);
    List<Payment> findByContractId(ContractId contractId);
    Money sumPaidAmountByContract(ContractId contractId);
}
```
