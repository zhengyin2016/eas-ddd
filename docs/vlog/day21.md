# Day 21 — 培训上下文聚合设计

**日期**：领域建模 Day 21
**作者**：张毅

---

昨天完成了领域分析建模，识别出12个领域概念。今天的工作是聚合设计——这是领域设计建模的核心步骤，需要像庖丁解牛一样，将这些概念逐一解剖，分类为实体或值对象，然后根据它们之间的关系确定聚合边界。

第一步是识别值对象。值对象的判断标准是：体现单位概念、枚举概念，或者内聚但不需要独立管理生命周期。TicketStatus显然是枚举，票的状态包括Available、WaitForConfirm、Confirmed、Cancelled。TicketOwner体现了一个单位概念——票的拥有者，由员工ID和姓名组成。OperationType也是枚举——提名、确认、取消。StateTransit和Operator同样是值对象。

第二步是识别实体。这里有一个关键的判断：ProgramOwner、Coordinator、Nominee、Trainee这些概念，虽然在统一语言中有明确的身份区分，但在培训上下文中，它们只持有员工ID，并不需要维护独立的生命周期。因此它们是值对象。Teacher也是同理——培训上下文不单独维护教师信息，教师只是培训的一个属性。而Filter和ValidDate需要单独管理生命周期，是实体。ValidDateAction和CancellingAction也是实体。

第三步是确定实体关系。这里要区分组合关系和OO聚合关系。Course组合多个Training——课程是培训的基础，培训离不开课程。Training组合多个Ticket——票属于培训。Training与Filter、ValidDate之间是OO聚合关系，它们虽然关联但可以独立管理。ValidDate聚合多个ValidDateAction，Ticket聚合多个CancellingAction和TicketHistory。Training聚合多个Candidate和Attendance。BlackList则完全独立。

第四步是确定聚合边界。聚合边界的设计原则是：一个聚合内保证一致性边界，跨聚合只能通过标识引用。经过分析，Learning聚合中的Course具有独立性——课程信息需要被多个培训共享，不适合作为Learning的内部实体。因此将Course分离为单独的聚合。

最终确定了12个聚合：Training、Course、Learning、Ticket、TicketHistory、Filter、ValidDate、ValidDateAction、CancellingAction、Candidate、Attendance、Blacklist。每个聚合都有自己的聚合根、标识和资源库。

聚合设计的过程让我深刻体会到，领域驱动设计的聚合不是简单的对象组合，而是对一致性边界的精心划定。跨聚合的引用只能通过标识，这看似增加了复杂度，实则保证了各聚合的独立性和可扩展性。明天将进入服务驱动设计，通过序列图发现领域服务。
