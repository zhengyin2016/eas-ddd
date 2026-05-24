# PM上下文聚合设计

> 设计日期：2026-05-24
> 设计师：吴斌、张毅、赵敏

## 聚合设计原则

1. **一致性边界**：每个聚合保证其内部不变性规则
2. **小聚合**：聚合尽量小，避免性能问题
3. **引用ID**：聚合之间只通过ID引用，避免对象引用
4. **最终一致性**：跨聚合的依赖通过领域事件处理

## 聚合设计

### 1. Project聚合

**职责**：管理项目的生命周期、任务和里程碑

**结构**：
```
Project (聚合根)
├── ProjectStatus (值对象)
├── List<Milestone> (实体集合)
└── List<Task> (实体集合)
```

**不变性规则**：
- 状态转换必须遵守状态机规则
- 已关闭的项目不能创建Task
- 项目预算不能为负数
- 结束日期不能早于开始日期

**关键行为**：
```java
// Project.java
public class Project {
    public void approve() { /* PREPARING → APPROVED */ }
    public void start() { /* APPROVED → IN_PROGRESS */ }
    public void suspend() { /* IN_PROGRESS → SUSPENDED */ }
    public void resume() { /* SUSPENDED → IN_PROGRESS */ }
    public void close() { /* → CLOSED */ }

    public Task createTask(String name, String assigneeId) {
        if (this.status == ProjectStatus.CLOSED) {
            throw new IllegalStateException("已关闭的项目不能创建任务");
        }
        // ...
    }

    public Milestone addMilestone(String name, LocalDate plannedDate) {
        // ...
    }
}
```

---

### 2. Assignment聚合

**职责**：管理员工到项目的分配关系

**结构**：
```
Assignment (聚合根)
├── AssignmentRole (值对象)
└── EmployeeId (值对象，引用HR上下文)
```

**不变性规则**：
- 分配比例（allocation）必须在0-100之间
- 同一员工在同一项目上不能有时间重叠的分配
- 分配时间段不能超出所属项目的时间段

**关键行为**：
```java
// Assignment.java
public class Assignment {
    public void updateAllocation(int newAllocation) {
        if (newAllocation < 0 || newAllocation > 100) {
            throw new IllegalArgumentException("分配比例必须在0-100之间");
        }
        this.allocation = newAllocation;
    }

    public void release() {
        // 释放分配，标记为已释放
    }
}
```

**跨聚合验证**：
- 创建时需验证项目时间段（通过应用服务查询Project）
- 项目时间段变化时需检查并更新受影响的Assignment（通过应用服务）

---

### 3. Issue聚合

**职责**：管理项目问题的生命周期

**结构**：
```
Issue (聚合根)
├── IssueStatus (值对象)
├── IssueSeverity (值对象)
└── ProjectId (值对象，引用Project聚合)
```

**不变性规则**：
- 严重程度（severity）创建后不能修改
- 状态转换必须遵守状态机：OPEN → IN_PROGRESS → RESOLVED → CLOSED
- 已关闭的Issue不能重新打开

**关键行为**：
```java
// Issue.java
public class Issue {
    public void assign(String assigneeId) {
        if (this.status == IssueStatus.CLOSED) {
            throw new IllegalStateException("已关闭的问题不能分配");
        }
        this.assigneeId = assigneeId;
        this.status = IssueStatus.IN_PROGRESS;
    }

    public void resolve(String resolution) {
        if (this.status != IssueStatus.IN_PROGRESS) {
            throw new IllegalStateException("只有进行中的问题才能解决");
        }
        this.resolution = resolution;
        this.status = IssueStatus.RESOLVED;
    }

    public void close() {
        if (this.status != IssueStatus.RESOLVED) {
            throw new IllegalStateException("只有已解决的问题才能关闭");
        }
        this.status = IssueStatus.CLOSED;
    }
}
```

---

### 4. Iteration聚合

**职责**：管理项目迭代的时间盒

**结构**：
```
Iteration (聚合根)
├── IterationStatus (值对象)
└── ProjectId (值对象，引用Project聚合)
```

**不变性规则**：
- 开始日期不能晚于结束日期
- 状态转换：PLANNED → ACTIVE → COMPLETED
- 已完成的迭代不能重新激活

**关键行为**：
```java
// Iteration.java
public class Iteration {
    public void start() {
        if (this.status != IterationStatus.PLANNED) {
            throw new IllegalStateException("只有计划中的迭代才能启动");
        }
        this.status = IterationStatus.ACTIVE;
    }

    public void complete() {
        if (this.status != IterationStatus.ACTIVE) {
            throw new IllegalStateException("只有进行中的迭代才能完成");
        }
        this.status = IterationStatus.COMPLETED;
    }
}
```

---

## 聚合依赖关系

```
┌─────────────────────────────────────────────────────────┐
│                    PM限界上下文                          │
│                                                          │
│  ┌──────────────┐         ┌──────────────┐             │
│  │   Project    │         │  Assignment  │             │
│  │   (聚合根)   │◄────────│   (聚合根)   │             │
│  │              │  1:N    │              │             │
│  │  - Milestone │  projectId             │             │
│  │  - Task      │         │  - employeeId│────┐        │
│  └──────────────┘         └──────────────┘    │        │
│         │                                      │        │
│         │ 1:N                                  │        │
│         │                                      │        │
│  ┌──────────────┐         ┌──────────────┐    │        │
│  │    Issue     │         │   Iteration  │    │        │
│  │   (聚合根)   │         │   (聚合根)   │    │        │
│  └──────────────┘         └──────────────┘    │        │
│                                                │        │
└────────────────────────────────────────────────┘        │
                                                         │
                                                         ▼
                                                ┌──────────────────┐
                                                │   HR限界上下文    │
                                                │                  │
                                                │   Employee       │
                                                │   (聚合根)       │
                                                └──────────────────┘
```

## 聚合与持久化映射

### Project聚合 → 数据表

| 领域对象 | 表名 | 说明 |
|---------|------|------|
| Project | project | 主表 |
| Task | task | 一对多关联 |
| Milestone | milestone | 一对多关联 |

### Assignment聚合 → 数据表

| 领域对象 | 表名 | 说明 |
|---------|------|------|
| Assignment | assignment | 单表 |

### Issue聚合 → 数据表

| 领域对象 | 表名 | 说明 |
|---------|------|------|
| Issue | issue | 单表 |

### Iteration聚合 → 数据表

| 领域对象 | 表名 | 说明 |
|---------|------|------|
| Iteration | iteration | 单表 |

## 并发控制策略

- **乐观锁**：所有聚合使用version字段进行乐观锁控制
- **锁获取**：Repository层实现乐观锁，更新时检查version
- **冲突处理**：应用层捕获OptimisticLockingFailureException，提示用户重试

## 聚合设计决策记录

| 决策 | 理由 | 替代方案 |
|-----|------|---------|
| Task属于Project聚合 | Task生命周期完全依赖Project，不被其他聚合引用 | Task独立聚合（增加复杂度） |
| Assignment独立聚合 | 连接Project和Employee，独立生命周期 | Assignment属于Project（跨聚合困难） |
| Issue独立聚合 | Issue生命周期独立，可被单独查询和管理 | Issue属于Project（查询不便） |
| Iteration独立聚合 | 迭代是独立的时间盒概念，有独立状态机 | Iteration属于Project（概念混淆） |
