# Day 25 — 其余上下文的高层领域模型

**日期**：领域建模 Day 25
**作者**：张毅

---

培训上下文的领域建模已经完成。今天的工作是为EAS系统的其余11个限界上下文建立高层领域模型。所谓"高层"，是指只列出每个上下文的核心聚合，不编写代码——详细的领域建模将在后续迭代中，由各特性团队根据业务优先级逐步展开。

订单上下文（核心子领域）的核心聚合：MarketRequirement（市场需求）、Order（订单）、CustomerRequirement（客户需求）、Proposal（建议书）。这个上下文管理从市场需求到客户订单的完整流程。

合同上下文（核心子领域）的核心聚合：Contract（合同）、ContractItem（合同条款）、ContractArchive（合同归档）。合同归档与文件共享上下文协作，属于客户方/供应方模式。

员工上下文（核心子领域）的核心聚合：Employee（员工）、WorkLog（工作日志）、Attendance（考勤）。工作日志和考勤是从原来的独立业务主体合并而来的。

储备人才上下文（核心子领域）的核心聚合：Talent（储备人才）、TalentProfile（人才档案）、ProjectExperience（项目经验）。注意这里的ProjectExperience与项目上下文的项目信息是不同的知识语境。

招聘上下文（核心子领域）的核心聚合：JobPosition（职位）、Interview（面试）、Offer。从发布职位到面试再到录用，形成完整的招聘流程。

项目上下文（核心子领域）的核心聚合：Project（项目）、Iteration（迭代）、Issue（问题）、ProjectMember（项目成员）。问题虽然在概念上是项目的子概念，但聚合设计时可以独立。

决策分析上下文（核心子领域）采用弱化的菱形对称架构，没有传统意义上的聚合，直接针对统计数据进行操作。其核心概念包括：StatisticsReport（统计报表）、Dashboard（仪表盘）、DataCube（数据立方体）。

文件共享上下文（支撑子领域）实现为基础层的库，核心聚合：FileService（文件服务）、FilePathMapping（文件路径映射）。

OA集成上下文（支撑子领域）的核心聚合：MailTemplate（邮件模板）、NotificationEvent（通知事件）。事件订阅者订阅其他上下文发布的应用事件。

组织上下文（通用子领域）的核心聚合：Department（部门）、Team（团队）、Position（岗位）。

认证上下文（通用子领域）的核心聚合：User（用户）、Role（角色）、Permission（权限）。

至此，领域建模阶段的工作暂告一段落。培训上下文的详细建模已经完成，其余上下文的高层模型为后续迭代提供了方向。接下来将进入代码模型的实现阶段。
