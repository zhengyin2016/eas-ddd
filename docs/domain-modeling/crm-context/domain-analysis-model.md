# CRM上下文 - 领域分析模型

## 概述

客户关系管理（CRM）上下文负责管理客户关系全生命周期：从客户线索到商机到合同到回款。

## 领域对象识别

### 核心名词提取

| 名词 | 类型 | 说明 |
|------|------|------|
| Customer | 聚合根 | 客户，CRM的核心实体 |
| Contact | 实体 | 联系人，属于Customer |
| Opportunity | 聚合根 | 商机，跟踪销售机会 |
| Contract | 聚合根 | 合同，签订的订单 |
| PaymentPlan | 实体 | 回款计划，属于Contract |
| Payment | 聚合根 | 回款记录 |

### 值对象识别

| 值对象 | 类型 | 取值范围 |
|--------|------|---------|
| CustomerLevel | 枚举 | A, B, C, D |
| CustomerSource | 枚举 | REFERRAL, EXHIBITION, WEBSITE, COLD_CALL, OTHER |
| OpportunityStage | 枚举 | INITIAL_CONTACT, NEEDS_CONFIRMATION, PROPOSAL, NEGOTIATION, CONTRACT_SIGNING |
| ContractStatus | 枚举 | DRAFT, UNDER_REVIEW, APPROVED, ACTIVE, COMPLETED, TERMINATED |
| PaymentStatus | 枚举 | PLANNED, PAID, OVERDUE, CANCELLED |

## 聚合划分

### Customer 聚合

| 领域对象 | 类型 | 属性 | 行为 |
|---------|------|------|------|
| Customer | 聚合根 | id, name, industry, level, source, contactName, contactPhone, address, creatorId, createdAt | create(), update(), upgradeLevel(), downgradeLevel() |
| CustomerLevel | 值对象 | code(A/B/C/D), description | - |
| CustomerSource | 值对象 | code(REFERRAL/EXHIBITION/WEBSITE/COLD_CALL/OTHER), description | - |
| Contact | 实体 | id, name, phone, email, position, isPrimary | update(), setPrimary() |

### Opportunity 聚合

| 领域对象 | 类型 | 属性 | 行为 |
|---------|------|------|------|
| Opportunity | 聚合根 | id, customerId, title, estimatedAmount, stage, probability, expectedCloseDate, ownerId, createdAt, updatedAt | create(), advanceStage(), markWon(), markLost() |
| OpportunityStage | 值对象 | code, probability | - |

### Contract 聚合

| 领域对象 | 类型 | 属性 | 行为 |
|---------|------|------|------|
| Contract | 聚合根 | id, customerId, opportunityId, title, amount, status, signDate, startDate, endDate, createdAt, updatedAt | create(), submitForReview(), approve(), reject(), activate(), complete() |
| ContractStatus | 值对象 | code(DRAFT/UNDER_REVIEW/APPROVED/ACTIVE/COMPLETED/TERMINATED), description | - |
| PaymentPlan | 实体 | id, contractId, amount, dueDate, status, createdAt | createPlan(), markPaid(), markOverdue() |

### Payment 聚合

| 领域对象 | 类型 | 属性 | 行为 |
|---------|------|------|------|
| Payment | 聚合根 | id, contractId, amount, paymentDate, paymentMethod, status, createdAt, updatedAt | record(), confirm(), markOverdue() |
| PaymentStatus | 值对象 | code(PLANNED/PAID/OVERDUE/CANCELLED), description | - |

## 业务规则

### 客户分级规则

| 等级 | 条件 | 权益 |
|------|------|------|
| A级 | 累计交易额≥100万 或 年交易次数≥10次 | 专属客户经理、优先响应 |
| B级 | 累计交易额≥50万 或 年交易次数≥5次 | 优先响应 |
| C级 | 累计交易额≥10万 或 年交易次数≥1次 | 标准服务 |
| D级 | 新客户 | 标准服务 |

客户等级每季度重新评估一次。

### 商机阶段与赢率

| 阶段 | 赢率 | 说明 |
|------|------|------|
| INITIAL_CONTACT | 20% | 初步接触 |
| NEEDS_CONFIRMATION | 40% | 需求确认 |
| PROPOSAL | 60% | 方案报价 |
| NEGOTIATION | 80% | 商务谈判 |
| CONTRACT_SIGNING | 95% | 合同签订 |

商机阶段只能向前推进，不能回退。

### 合同状态流转

```
DRAFT → UNDER_REVIEW → APPROVED → ACTIVE → COMPLETED
   ↓         ↓              ↑
   └─────────┴──────────────┘
           ↓
       TERMINATED (任意时刻可终止)
```

### 回款规则

- 回款计划逾期30天自动标记为OVERDUE
- 回款确认后更新合同已回款金额
- 全部回款完成后合同状态自动变为COMPLETED

## 聚合间关系

```
Customer (1) ←─── (N) Opportunity
Opportunity (1) ←─── (1) Contract
Contract (1) ←─── (N) PaymentPlan
Contract (1) ←─── (N) Payment
```

## 领域事件

| 事件 | 触发条件 | 消费者 |
|------|---------|--------|
| ContractSignedEvent | 合同激活（ACTIVE状态） | PM上下文（创建项目） |
| PaymentConfirmedEvent | 回款确认（PAID状态） | Customer聚合（评估等级）、财务系统 |
| OpportunityWonEvent | 商机赢单（markWon） | 统计服务 |
| OpportunityLostEvent | 商机输单（markLost） | 统计服务 |
