# 培训上下文聚合设计

> 来源：《解构领域驱动设计》第 20.3.4 节 — 图 20-52 至图 20-56
> 方法：庖丁解牛式的聚合设计过程

---

## 1. 识别值对象（图 20-52）

值对象的判断标准：体现单位概念、枚举概念，或内聚但不需要独立管理生命周期。

| 值对象 | 类型 | 说明 |
|--------|------|------|
| TicketStatus | 枚举 | 票的状态：Available, WaitForConfirm, Confirmed, Cancelled |
| TicketOwner | 单位 | 票的拥有者，由 employeeId 和 name 组成 |
| OperationType | 枚举 | 操作类型：Nomination, Confirmation, Cancellation |
| StateTransit | 单位 | 状态转换，由 from 和 to 两个 TicketStatus 组成 |
| Operator | 单位 | 操作者，由 employeeId 和 name 组成 |
| ProgramOwner | 单位 | 项目负责人，培训上下文中只持有员工ID |
| Coordinator | 单位 | 协调者，培训上下文中只持有员工ID |
| Nominee | 单位 | 被提名人，培训上下文中只持有员工ID |
| Trainee | 单位 | 学员，培训上下文中只持有员工ID |
| Teacher | 单位 | 教师，培训上下文中不单独维护教师信息 |
| Nominator | 单位 | 提名者，由 employeeId 和 name 组成 |

**关键判断**：ProgramOwner、Coordinator、Nominee、Trainee 在统一语言中有明确的身份区分，但在培训上下文中它们只持有员工ID，不需要维护独立的生命周期，因此是值对象。Teacher 同理——培训上下文不单独维护教师信息。

---

## 2. 识别实体（图 20-53）

实体的判断标准：需要单独管理生命周期，拥有唯一标识。

| 实体 | 说明 |
|------|------|
| Training | 培训活动的主体，需要独立管理 |
| Course | 课程信息，需要独立维护 |
| Ticket | 票，拥有独立的状态和生命周期 |
| TicketHistory | 票历史，需要追踪和持久化 |
| Filter | 过滤器，需要单独管理配置和规则 |
| ValidDate | 有效日期，需要单独管理生命周期 |
| ValidDateAction | 有效日期动作，需要独立管理 |
| CancellingAction | 取消动作，需要独立管理 |
| Candidate | 候选人，需要独立管理 |
| Attendance | 出勤记录，需要独立管理 |
| Blacklist | 黑名单，需要独立管理 |
| Learning | 学习记录，需要独立管理 |

---

## 3. 确定实体关系（图 20-54）

区分组合关系和聚合关系：

### 组合关系（整体与部分的生命周期一致）

| 整体 | 部分 | 说明 |
|------|------|------|
| Course | Training | 课程组合多个培训，培训离不开课程 |
| Training | Ticket | 培训组合多张票，票属于培训 |

### OO 聚合关系（部分可独立存在）

| 整体 | 部分 | 说明 |
|------|------|------|
| Training | Filter | 培训关联过滤器，但过滤器可独立管理 |
| Training | ValidDate | 培训关联有效日期，但有效日期可独立管理 |
| ValidDate | ValidDateAction | 有效日期聚合多个有效日期动作 |
| Ticket | CancellingAction | 票聚合多个取消动作 |
| Ticket | TicketHistory | 票聚合多个票历史 |
| Training | Candidate | 培训聚合多个候选人 |
| Training | Attendance | 培训聚合多个出勤记录 |
| Course | Learning | 课程关联多个学习记录 |

### 独立存在

| 实体 | 说明 |
|------|------|
| Blacklist | 黑名单完全独立，不依赖其他实体 |

---

## 4. 确定聚合边界（图 20-55 / 图 20-56）

### 关键决策：Learning 中的 Course 独立性

Learning 聚合中的 Course 具有独立性——课程信息需要被多个培训共享，不适合作为 Learning 的内部实体。将 Course 分离为单独的聚合。Learning 只持有 CourseId 作为引用。

### 最终 12 个聚合

| # | 聚合根 | 聚合内实体 | 聚合内值对象 | 资源库 |
|---|--------|-----------|------------|--------|
| 1 | Training | Training | Course(值对象引用), Teacher(值对象) | TrainingRepository |
| 2 | Course | Course | — | CourseRepository |
| 3 | Learning | Learning | — | LearningRepository |
| 4 | Ticket | Ticket | TicketStatus, TicketOwner, Nominator | TicketRepository |
| 5 | TicketHistory | TicketHistory | OperationType, StateTransit, Operator | TicketHistoryRepository |
| 6 | Filter | Filter | — | FilterRepository |
| 7 | ValidDate | ValidDate | — | ValidDateRepository |
| 8 | ValidDateAction | ValidDateAction | — | ValidDateActionRepository |
| 9 | CancellingAction | CancellingAction | — | CancellingActionRepository |
| 10 | Candidate | Candidate | — | CandidateRepository |
| 11 | Attendance | Attendance | — | AttendanceRepository |
| 12 | Blacklist | Blacklist | — | BlacklistRepository |

### 聚合间引用规则

- 跨聚合引用只能通过标识（如 TrainingId, CourseId, TicketId）
- 不允许跨聚合的对象引用
- 一致性边界限定在聚合内部

```
Training ──[TrainingId]──> Course        (标识引用)
Ticket   ──[TrainingId]──> Training      (标识引用)
Ticket   ──[TicketId]────> TicketHistory (标识引用，独立聚合)
Learning ──[CourseId]────> Course        (标识引用)
Candidate──[TrainingId]──> Training      (标识引用)
Filter   ──[TrainingId]──> Training      (标识引用)
ValidDate──[TrainingId]──> Training      (标识引用)
ValidDateAction──[ValidDateId]──> ValidDate (标识引用)
CancellingAction──[TicketId]──> Ticket   (标识引用)
Attendance──[TrainingId]──> Training     (标识引用)
```
