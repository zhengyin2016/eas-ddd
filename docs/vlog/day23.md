# Day 23 — TDD驱动聚合设计：Ticket

**日期**：领域建模 Day 23
**作者**：张毅

---

今天是TDD实践的第一天。我按照书中描述的过程，为Ticket聚合编写测试用例，通过测试驱动出精确的方法签名和依赖对象。

第一个测试用例是验证票在提名前的状态必须为Available。这看似简单，但它驱动出了Ticket的核心行为约束：只有状态为Available的票才能被提名。测试创建了两个候选人，先对票进行一次提名（成功），然后再次提名（应失败）。这让我意识到validateAvailable()方法必须在nominate()内部被调用，作为前置条件检查。

第二个测试用例验证提名后票的状态变更为WaitForConfirm。这个测试驱动出了Ticket的nominate方法签名：nominate(Candidate candidate, Nominator nominator)。提名操作不仅改变了票的状态，还要记录被提名人的ID。Candidate的asOwner()方法将候选人转换为票的拥有者，这里驱动出了TicketOwner值对象。

第三个测试用例验证提名操作生成票历史记录。这个测试最有意思——它要求Ticket的nominate()方法返回一个TicketHistory对象。为了满足这个断言，我必须创建TicketHistory聚合，以及它依赖的值对象和枚举：OperationType枚举（Nomination、Confirmation、Cancellation）、StateTransit值对象（记录状态从Available到WaitForConfirm的转换）、Operator值对象（记录操作者的员工ID和姓名）。

整个TDD过程驱动出了以下类型：

TicketStatus枚举——票的四种状态。TicketOwner值对象——票的拥有者，由employeeId和name组成。Nominator值对象——提名者，同样由employeeId和name组成。OperationType枚举——操作类型。StateTransit值对象——状态转换记录。Operator值对象——操作者。TicketHistory聚合——票历史，包含静态工厂方法from()。

最重要的是，方法签名从最初的nominate(candidate)演变为nominate(candidate, nominator)——因为测试要求历史记录中包含操作者信息，而操作者就是提名者。这个演进过程完美地诠释了TDD对设计的驱动作用。

三个测试用例，驱动出七个新类型和一个方法签名的演进。这就是TDD的威力——测试不是在验证已有代码，而是在驱动代码的设计。
