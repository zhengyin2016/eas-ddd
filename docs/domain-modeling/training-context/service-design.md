# 培训上下文服务驱动设计

> 来源：《解构领域驱动设计》第 20.3.4 节
> 以"提名候选人"业务服务为例

---

## 1. 任务树

### "提名候选人"业务服务的任务分解

```
提名候选人（业务服务）
├── 确定候选人是否已经参加过该课程
│   ├── 获取该培训对应的课程
│   └── 确定课程学习记录是否有该候选人
├── 如果未参加，则提名候选人
│   ├── 获得培训票
│   ├── 提名
│   └── 保存票的状态
└── 发送提名通知
    ├── 获取通知邮件模板
    ├── 组装提名通知内容
    └── 发送通知
```

### 任务与聚合的映射

| 任务 | 参与的聚合 | 说明 |
|------|-----------|------|
| 获取该培训对应的课程 | Training → Course | Training 持有 CourseId，需查询 Course |
| 确定课程学习记录是否有该候选人 | Learning, Course | Learning 持有 CourseId 和 EmployeeId |
| 获得培训票 | Ticket | Ticket 持有 TrainingId |
| 提名 | Ticket, Candidate | Ticket.nominate(candidate, nominator) |
| 保存票的状态 | Ticket, TicketHistory | 跨聚合协调：修改票状态 + 记录历史 |
| 获取通知邮件模板 | MailTemplate (待定) | 需要确认归属上下文 |
| 组装提名通知内容 | — | 组装逻辑 |
| 发送通知 | OA 集成上下文 | 由 OA 集成上下文负责 |

---

## 2. 序列图

### "提名候选人"的协作序列

```
Coordinator -> NominationAppService: nominateCandidate(trainingId, candidate, nominator)
NominationAppService -> TrainingRepository: findById(trainingId)
TrainingRepository --> NominationAppService: training
NominationAppService -> CourseRepository: findById(training.courseId())
CourseRepository --> NominationAppService: course
NominationAppService -> LearningRepository: findByCourseIdAndEmployeeId(course.courseId(), candidate.employeeId())
LearningRepository --> NominationAppService: Optional<Learning>
alt 候选人未学习过该课程
    NominationAppService -> TicketService: nominateCandidate(ticketId, candidate, nominator)
    TicketService -> TicketRepository: findById(ticketId)
    TicketRepository --> TicketService: ticket
    TicketService -> Ticket: nominate(candidate, nominator)
    Ticket --> TicketService: ticketHistory
    TicketService -> TicketRepository: save(ticket)
    TicketService -> TicketHistoryRepository: save(ticketHistory)
end
NominationAppService -> NotificationClient: sendNominationNotification(candidate, training)
```

---

## 3. 发现领域服务

### NominationService / TicketService

序列图揭示了跨聚合的协调逻辑：

1. **提名操作**需要同时修改 Ticket 的状态并创建 TicketHistory 记录
2. 这两个聚合（Ticket 和 TicketHistory）之间需要协调一致性
3. 协调逻辑不属于任何单个聚合，应该由领域服务承担

因此识别出 **TicketService** 领域服务：

```java
public class TicketService {
    private TicketRepository ticketRepository;
    private TicketHistoryRepository ticketHistoryRepository;

    public void nominateCandidate(TicketId ticketId, Candidate candidate, Nominator nominator) {
        Ticket ticket = ticketRepository.findById(ticketId)
            .orElseThrow(() -> new TicketException("Ticket not found"));
        ticket.nominate(candidate, nominator);
        ticketRepository.save(ticket);
        TicketHistory history = TicketHistory.from(ticket, OperationType.Nomination,
            new Operator(nominator.employeeId(), nominator.name()));
        ticketHistoryRepository.save(history);
    }
}
```

---

## 4. 迭代演进：发现 MailTemplate 聚合

序列图中的"获取通知邮件模板"暴露了一个之前遗漏的领域概念：**MailTemplate**（邮件模板）。

### 分析

- 发送通知需要邮件模板来组装通知内容
- 邮件模板包含模板ID、模板名称、模板内容、模板变量等信息
- 邮件通知的发送由 OA 集成上下文负责，但模板的管理可能属于培训上下文

### 初步结论

MailTemplate 可能属于以下之一：
1. 培训上下文的独立聚合
2. OA 集成上下文的一部分
3. 共享的配置数据

这个问题需要在后续迭代中根据具体的业务需求来确定。这个发现也印证了书中所说的：**领域建模很难一蹴而就，需要不断迭代演进**。

---

## 5. 层次映射

| 层次 | 组件 | 职责 |
|------|------|------|
| 应用服务 | NominationAppService | 编排任务树，协调领域服务和资源库 |
| 领域服务 | TicketService | 跨聚合协调（Ticket + TicketHistory） |
| 聚合根 | Ticket | 提名行为（nominate） |
| 聚合根 | TicketHistory | 静态工厂方法（from） |
| 资源库 | TicketRepository, TicketHistoryRepository, LearningRepository | 持久化 |
| 南向网关 | NotificationClient | 调用 OA 集成上下文发送通知 |
