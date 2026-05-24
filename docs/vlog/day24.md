# Day 24 - CRM聚合设计与服务驱动

**日期**: 第24天
**参与者**: 张毅、郑丽、孙磊
**主题**: CRM聚合设计 + 服务驱动设计

"今天要把聚合设计和服务驱动设计都完成。"我在白板上写道。

"一天两项？"郑丽有些惊讶。

"对，因为CRM的聚合相对清晰，而且我们已经有了HR和PM的经验。"我解释，"先做聚合设计。"

我指着白板上的Customer聚合。"Customer是聚合根，Contact是实体。这里有个问题：Contact需要独立ID吗？"

"需要。"孙磊说，"因为一个客户有多个联系人，我们可能需要单独查询某个联系人，或者把某个联系人设为主要联系人。"

"对，所以Contact是实体不是值对象。"我确认，"Contact有自己的ID和isPrimary属性。"

"那CustomerLevel和CustomerSource呢？"郑丽问。

"这两个是值对象。"我在白板上标记，"客户等级A/B/C/D是有限的枚举，客户来源（推荐、展会、网站、电销等）也是。用Java Record实现。"

接下来是Opportunity聚合。

"Opportunity的stage字段类型是什么？"郑丽问。

"是OpportunityStage值对象。"我回答，"但这里有个特殊之处——每个阶段对应一个赢率。昨天孙磊说了，初步接触20%，需求确认40%..."

"我有个问题。"孙磊插话，"赢率是固定的还是可配置的？"

"好问题。"我思考了一下，"从业务角度看，赢率应该是可配置的，不同公司、不同产品可能有不同的赢率。但为了简化，我们可以先把赢率编码在OpportunityStage枚举里。"

"那商机推进阶段时，赢率自动更新？"郑丽问。

"对。"我点头，"advanceStage()方法应该同时更新stage和probability。"

讨论到Contract聚合时，大家产生了分歧。

"PaymentPlan应该是实体还是值对象？"郑丽问。

"实体。"孙磊说，"因为一个合同有多个回款计划期，每期有自己的金额、日期、状态。"

"我同意。"我补充，"而且PaymentPlan的状态可以变化（从计划中到已支付到逾期），这需要可变性。"

"那Contract和PaymentPlan是一对多？"

"对。"我在白板上画出关系，"Contract聚合根包含多个PaymentPlan实体。但要注意，PaymentPlan不能脱离Contract存在，它是Contract的一部分。"

最后是Payment聚合。

"Payment应该是独立的聚合根。"我强调，"虽然它记录的是合同的回款，但回款记录可能被独立查询、统计，比如财务系统的对账。"

"那Payment和Contract是什么关系？"

"Payment有contractId字段指向Contract。"我解释，"这是聚合间的引用，不是包含。"

下午开始服务驱动设计。

"CRM有哪些业务服务？"我问。

孙磊想了想："创建客户、更新客户、创建商机、推进商机阶段、创建合同、审核合同、制定回款计划、记录回款、查询客户信息、分析销售漏斗。"

"10个服务。"我数了一下，"来，我们分析每个服务的输入输出。"

**创建客户**：
- 输入：name, industry, source, contactName, contactPhone, address, creatorId
- 输出：customerId

**创建商机**：
- 输入：customerId, title, estimatedAmount, expectedCloseDate, ownerId
- 输出：opportunityId
- 业务规则：初始阶段为INITIAL_CONTACT，赢率20%

**推进商机阶段**：
- 输入：opportunityId, newStage
- 输出：更新后的商机
- 业务规则：阶段只能向前推进，不能回退；赢率根据阶段自动更新

**创建合同**：
- 输入：customerId, opportunityId, title, amount, signDate, startDate, endDate
- 输出：contractId
- 业务规则：初始状态为DRAFT

**审核合同**：
- 输入：contractId, approve
- 输出：更新后的合同
- 业务规则：审核通过后状态变为APPROVED，拒绝后变为DRAFT

**记录回款**：
- 输入：contractId, amount, paymentDate, paymentMethod
- 输出：paymentId
- 业务规则：初始状态为PLANNED

"销售漏斗分析呢？"郑丽问，"这个服务比较复杂。"

"对。"我点头，"销售漏斗分析需要统计各阶段的商机数量和金额，计算转化率。这个服务应该放在应用层，因为它需要跨聚合查询。"

"客户分级呢？"孙磊问，"你说客户等级会根据交易自动调整。"

"这个规则应该放在Customer领域服务里。"我回答，"比如有一个upgradeLevel()方法，根据累计交易额判断是否升级。但什么时候触发这个方法呢？"

"回款记录确认后？"郑丽建议。

"对。"我点头，"Payment确认后，可以发布一个领域事件，Customer聚合监听这个事件，然后触发等级调整。"

傍晚时分，CRM的服务驱动设计基本完成。

"今天效率很高。"我看着白板，"两个阶段的设计都完成了。"

郑丽开始写《CRM聚合设计》和《CRM服务驱动设计》文档，孙磊继续确认业务规则。

"明天就可以开始编码了。"我合上笔记本，"CRM上下文的领域建模阶段即将完成。"

---

**今日产出**：
- CRM聚合设计（4聚合、3实体、6值对象）
- CRM服务驱动设计（10个业务服务）
- 明确商机阶段与赢率的对应关系
- 明确PaymentPlan为Contract内部实体、Payment为独立聚合根

**关键决策**：
- 商机阶段推进时自动更新赢率
- 回款确认后触发客户等级重新评估
- 销售漏斗分析放在应用层
- PaymentPlan不能脱离Contract存在

**明日计划**：
- 开始CRM上下文编码实现
- 完成领域层、应用层、基础设施层
- 单元测试
