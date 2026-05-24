# PM上下文领域分析模型

> 建模日期：2026-05-24
> 建模师：吴斌、张毅、赵敏

## 领域对象清单

| 领域对象 | 类型 | 所属聚合 | 属性 | 行为 |
|---------|------|---------|------|------|
| Project | 聚合根 | project | id, name, customerId, contractId, pmId, status, budget, startDate, endDate | create(), approve(), start(), suspend(), resume(), close() |
| ProjectStatus | 值对象 | project | PREPARING, APPROVED, IN_PROGRESS, SUSPENDED, CLOSED | - |
| Milestone | 实体 | project | id, name, plannedDate, actualDate, status | complete() |
| Task | 实体 | project | id, name, assigneeId, status, priority, estimatedHours, actualHours | create(), start(), complete() |
| TaskStatus | 值对象 | task | TODO, IN_PROGRESS, DONE | - |
| Assignment | 聚合根 | assignment | id, projectId, employeeId, role, allocation, startDate, endDate | assign(), release(), updateAllocation() |
| AssignmentRole | 值对象 | assignment | PM, TECH_LEAD, DEVELOPER, TESTER, DESIGNER | - |
| Issue | 聚合根 | issue | id, projectId, title, description, severity, priority, assigneeId, status | create(), assign(), resolve(), close() |
| IssueSeverity | 值对象 | issue | CRITICAL, MAJOR, MINOR | - |
| IssueStatus | 值对象 | issue | OPEN, IN_PROGRESS, RESOLVED, CLOSED | - |
| Iteration | 聚合根 | iteration | id, projectId, name, startDate, endDate, status | create(), start(), complete() |
| IterationStatus | 值对象 | iteration | PLANNED, ACTIVE, COMPLETED | - |

## 状态机定义

### Project状态机

```
PREPARING → APPROVED → IN_PROGRESS ←→ SUSPENDED → CLOSED
    ↓                              ↓
    └──────────────────────────────┘
```

**转换规则**：
- PREPARING → APPROVED：立项审批通过
- PREPARING → CLOSED：立项审批拒绝
- APPROVED → IN_PROGRESS：项目正式启动
- APPROVED → CLOSED：项目取消
- IN_PROGRESS → SUSPENDED：项目暂停
- SUSPENDED → IN_PROGRESS：项目恢复
- IN_PROGRESS → CLOSED：项目结项
- SUSPENDED → CLOSED：项目结项

### Issue状态机

```
OPEN → IN_PROGRESS → RESOLVED → CLOSED
```

### Task状态机

```
TODO → IN_PROGRESS → DONE
```

## 聚合关系图

```
┌─────────────────┐         ┌─────────────────┐
│   Project       │         │   Assignment    │
│   (聚合根)      │◄────────│   (聚合根)      │
│                 │  1:N    │                 │
│  - Milestone    │         │  - employeeId   │──→ HR上下文
│  - Task         │         └─────────────────┘
└─────────────────┘
        │
        │ 1:N
        ↓
┌─────────────────┐         ┌─────────────────┐
│     Issue       │         │   Iteration     │
│   (聚合根)      │         │   (聚合根)      │
└─────────────────┘         └─────────────────┘
```

## 跨上下文依赖

### PM → HR（通过ACL）

| PM领域对象 | 依赖的HR能力 | 交互方式 |
|-----------|-------------|---------|
| Assignment | 查询可用员工 | ACL防腐层 |
| Assignment | 验证员工可用性 | ACL防腐层 |
| Project | 结项后释放人员 | 领域事件 |

### CRM → PM

| CRM事件 | PM响应 |
|--------|--------|
| 合同签订 | 触发项目立项流程 |

## 不变性规则

1. **Project聚合**：
   - 状态转换必须遵守状态机规则
   - 已关闭的项目不能创建Task
   - 项目预算不能为负数

2. **Assignment聚合**：
   - 分配比例必须在0-100之间
   - 同一员工在同一项目的分配不能重叠（时间上）
   - 释放已关闭项目的分配

3. **Issue聚合**：
   - 严重程度(CRITICAL/MAJOR/MINOR)不能被修改
   - 已关闭的Issue不能重新打开

4. **Task实体**：
   - 实际工时不能小于0
   - 完成任务时必须填写实际工时

## 领域服务

| 领域服务 | 职责 |
|---------|------|
| ProjectDomainService | 跨聚合的项目业务规则验证 |
| AssignmentDomainService | 调用HR ACL验证员工可用性 |
| ResourceQueryService | 查询项目可用资源（组合多聚合数据） |
