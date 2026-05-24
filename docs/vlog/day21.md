# Day 21 - PM上下文服务设计：ACL防腐层

> 2026-05-24 | 张毅

---

今天是服务设计日。吴斌要定义PM上下文的应用服务和领域服务，特别是与HR上下文的交互。

"服务驱动设计的核心是识别服务职责，"吴斌开场说，"我们要区分三类服务：领域服务、应用服务、基础设施服务。"

他先在白板上画了三个层次：

```
┌────────────────────────────────────────┐
│          应用服务层（AppService）       │  ← 用例协调
├────────────────────────────────────────┤
│          领域服务层（DomainService）     │  ← 跨聚合业务规则
├────────────────────────────────────────┤
│          聚合根（Aggregate Root）        │  ← 单聚合业务规则
└────────────────────────────────────────┘
```

"应用服务是用例的直接实现，"吴斌说，"比如'创建项目'用例，对应ProjectAppService.createProject()方法。应用服务不包含业务逻辑，它只是协调——调用领域对象、调用仓储、发布事件。"

"那领域服务呢？"李华问。

"领域服务包含无法自然放在某个聚合里的业务逻辑，"吴斌说，"比如'分配员工'这个用例，需要检查HR上下文的员工可用性——这个逻辑不属于Assignment聚合（因为它涉及外部上下文），也不属于Project聚合。所以我们需要一个AssignmentDomainService。"

"ACL防腐层在哪里？"我问。

"ACL在基础设施层，"吴斌说，"AssignmentDomainService调用HRClientAdapter（ACL），HRClientAdapter再把数据转换成PM能理解的DTO。"

他在白板上画了一个完整的调用链：

```
ProjectAppService.assignMember()
    ↓
AssignmentDomainService.validateAndAssign()
    ↓
HRClientAdapter.queryAvailableEmployees()  ← ACL
    ↓
HR上下文北向服务
```

"这个ACL要做什么转换？"赵敏问。

"HR上下文返回的可能是Employee对象，包含很多PM不需要的字段，"吴斌说，"ACL要做两件事：第一，只保留PM需要的数据（employeeId、name、availableRatio）；第二，把HR的领域模型转换成PM的DTO（AvailableEmployeeDTO）。"

"这样PM上下文就不会被HR的变化影响，"我说，"如果HR的Employee结构变了，只需要改ACL，PM的领域模型不用变。"

吴斌点头，继续列应用服务清单：

**ProjectAppService**：
- createProject() - 创建项目
- approveProject() - 审批项目
- startProject() - 启动项目
- suspendProject() - 暂停项目
- resumeProject() - 恢复项目
- closeProject() - 结项
- queryProjectProgress() - 查询项目进度

**AssignmentAppService**：
- assignMember() - 分配成员
- releaseMember() - 释放成员
- updateAllocation() - 更新分配比例
- queryAvailableResources() - 查询可用资源

**IssueAppService**：
- createIssue() - 创建问题
- assignIssue() - 分配问题
- resolveIssue() - 解决问题
- closeIssue() - 关闭问题

**TaskAppService**（合并入ProjectAppService）：
- createTask() - 创建任务
- startTask() - 开始任务
- completeTask() - 完成任务

"TaskAppService为什么合并？"李华问。

"因为Task是Project聚合的一部分，操作Task必须通过Project聚合根，"吴斌说，"所以createTask()等方法放在ProjectAppService里更合理。"

接下来是领域服务：

**ProjectDomainService**：
- validateProjectDates() - 验证项目日期合法性
- checkProjectBudget() - 检查项目预算

**AssignmentDomainService**：
- validateAndAssign() - 验证并分配员工（调用HR ACL）
- checkAllocationConflict() - 检查分配冲突
- updateAssignmentsOnProjectDateChange() - 项目日期变化时更新分配

**ResourceQueryService**（查询服务）：
- queryProjectAvailableResources() - 查询项目可用资源（组合多聚合数据）

"ResourceQueryService是读模型服务，"吴斌说，"它可能需要组合Project、Assignment、甚至HR的数据。这种服务不需要事务保证，可以独立优化。"

下午，吴斌开始设计ACL接口。

"HRClientAdapter是PM调用HR的防腐层，"他说，"它需要实现HRClientPort接口，这样我们可以方便地替换实现（比如测试时用Mock）。"

他在白板上写了接口定义：

```java
public interface HRClientPort {
    AvailableEmployeeDTO queryEmployee(String employeeId);
    List<AvailableEmployeeDTO> queryAvailableEmployees(LocalDate startDate, LocalDate endDate);
    boolean isEmployeeAvailable(String employeeId, LocalDate startDate, LocalDate endDate);
}
```

"AvailableEmployeeDTO是PM的DTO，不是HR的，"我强调，"ACL负责转换。"

"对，"吴斌说，"HRClientAdapter的实现会调用HR的REST API，然后把HR的响应转换成AvailableEmployeeDTO。"

下午四点，服务设计基本完成。吴斌把所有服务和接口整理成一张完整的服务依赖图。

"感觉PM上下文的复杂度主要在跨上下文交互，"他说，"ACL是关键。"

"ACL是DDD防腐层的精髓，"我说，"它隔离了外部变化，保护了核心领域。"

**明天计划**：开始Java代码实现——领域层、应用层、基础设施层。

散会时，吴斌说："今天把服务的职责分清楚了，明天写代码应该会很顺。"

"服务设计是编码前的最后一步，"我说，"设计清楚了，代码就是水到渠成。"

Day 21，完成。
