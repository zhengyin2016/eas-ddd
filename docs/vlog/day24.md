# Day 24 — TDD驱动领域服务：TicketService

**日期**：领域建模 Day 24
**作者**：张毅

---

今天继续TDD实践，为TicketService领域服务编写测试。TicketService的职责是协调Ticket和TicketHistory两个聚合，处理提名候选人的完整操作。

第一个测试用例验证当票不存在时抛出异常。这个测试看似简单，但它驱动出了TicketService的依赖关系：TicketRepository和TicketHistoryRepository。测试使用Mockito模拟TicketRepository的findById方法返回空值，然后验证调用nominateCandidate时抛出TicketException。这个测试确保了领域服务对异常情况的处理。

第二个测试用例验证提名候选人成功时的完整流程。测试模拟TicketRepository返回一个状态为Available的Ticket，然后调用TicketService的nominateCandidate方法，最后验证TicketRepository.save()和TicketHistoryRepository.save()都被调用了一次。这个测试揭示了领域服务的完整职责：查找票、调用票的nominate方法、保存票、创建票历史、保存票历史。

编写这两个测试的过程让我发现了之前业务服务规约忽略的两个重要功能。第一，提名操作需要记录票的历史——这在原始的业务服务规约中并未提及，规约只说"保存票的状态"，但没有明确说要记录历史。第二，提名成功后，被提名人应该从候选人名单中移除——否则同一个人可以被反复提名。这两个遗漏体现了TDD对需求的反馈和修正作用：通过编写测试，我们被迫思考业务场景的完整性和边界条件，从而发现之前分析中的不足。

这也验证了书中强调的观点：领域建模是一个迭代过程。我们不应该期望一次就完成完美的设计，而是要在TDD的实践中不断发现和修正遗漏的需求。每一次编写测试，都是对业务规则的重新审视和深化理解。

明天将对其余11个上下文进行高层领域模型的概述。这些上下文暂时只列出核心聚合，不编写代码——详细的领域建模将在后续迭代中逐步展开。
