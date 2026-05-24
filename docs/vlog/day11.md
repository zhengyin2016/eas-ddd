# Day 11: 上下文映射与服务契约

**日期**: 2026-05-27
**阶段**: Phase 2 - 架构映射
**参与者**: 张毅、王强、赵敏、孙磊、郑丽

## Vlog：PM是协作中心

今天画上下文映射图——这是DDD最有趣的图，因为它揭示了系统各部分如何协作。

我在白板上画了六个圈：HR、PM、CRM、组织、认证、通知。

问大家：它们之间怎么调用？

王强说：PM分配人员的时候，需要查HR的员工信息。

赵敏说：PM立项的时候，可能需要关联CRM的合同。

孙磊说：CRM的合同签了，要通知PM开项目。

我点点头，在图上画了一堆箭头。然后我突然发现：**PM是协作中心**！

看看PM的依赖：
- 从HR获取员工可用性
- 从CRM获取客户/合同信息
- 向HR发送人员释放事件

而HR呢？HR基本是独立的——它不依赖PM和CRM，只是被调用。

CRM呢？CRM的合同签订后，需要触发PM的项目立项。所以CRM依赖PM。

## 上下游关系

我们明确了上下游关系：

| 关系 | 类型 | 说明 |
|-----|------|------|
| PM ← HR | 下游/上游 | PM是下游，通过ACL调用HR的OHS |
| PM ← CRM | 下游/上游 | PM是下游，通过ACL调用CRM的OHS |
| PM ← 组织 | 下游/上游 | PM是下游，查询组织架构 |
| CRM → PM | 上游/下游 | CRM发布"合同签订"事件，PM消费 |

郑丽问：为什么PM既是下游又是上游？

好问题。上下游是**场景相关**的。当PM调用HR时，PM是下游；当HR事件被PM消费时，HR是上游（发布者），PM是下游（订阅者）。

## 服务契约

下午我们定了每个核心上下文的REST API契约。

HR提供：
- GET /hr/employees - 查询员工列表
- GET /hr/employees/{id} - 查询员工详情
- GET /hr/employees/available - 查询可用员工
- POST /hr/employees - 创建员工

PM提供：
- POST /pm/projects - 创建项目
- GET /pm/projects/{id} - 查询项目
- POST /pm/projects/{id}/members - 分配成员

CRM提供：
- POST /crm/customers - 创建客户
- POST /crm/opportunities - 创建商机
- POST /crm/contracts - 创建合同

每个API我们都定义了请求体和响应体的JSON格式。

## 明天的预告

明天画系统分层架构——C4 Level 2。

---

*记录人：张毅*
