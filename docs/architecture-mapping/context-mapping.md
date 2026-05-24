# 上下文映射 (Context Mapping)

**编号**: EAS-ARCH-004
**版本**: 1.0
**日期**: 2026-05-27
**作者**: 张毅

## 概述

本文档定义EAS系统各限界上下文之间的映射关系，包括上下游关系、集成方式、防腐层设计。

## 上下文映射图

```
                        ┌─────────────────────────────────────────┐
                        │            认证上下文                    │
                        │      (ACL) │ (OHS)                       │
                        │         ╱ ╲                             │
                        └────────╱   ╲────────────────────────────┘
                                 ╱   ╲
                    ┌──────────╱     ╲──────────┐
                    │          ╱       ╲          │
            ┌───────┴──────┐  ╱         ╲  ┌──────┴──────┐
            │              ╱             ╲ │             │
    ┌───────┴────────┐  ╱                 ╲  │  ┌────────┴─────┐
    │                ╱                     ╲    │               │
┌───┴────┐      ┌───┴───┐              ┌───┴───┐│   ┌────────┴──┐  │
│ HR    │      │ 组织  │              │ CRM   │    │  通知      │  │
│ 上下文 │      │ 上下文│              │ 上下文 │    │  上下文     │  │
│(核心) │      │(通用) │              │ (核心) │    │  (支撑)     │  │
│       │      │       │              │        │    │            │  │
│OHS ◄──┼──────┼──OHS  │              │OHS ◄───┼────┼──OHS       │  │
│       │      │       │              │   ▲   │    │            │  │
│       │      │       │              │   │   │    │            │  │
└───────┘      └───────┘              └───┘   └────┴────────────┘  │
      ▲         ▲                             ▲   ▲               │
      │         │                             │   │               │
      │    ACL/ACL                           │   │               │
      │         │                             │   │               │
      └─────────┼─────────────────────────────┘   │               │
                │                                 │               │
           ┌────┴─────────────────────────────────┴───────────┐    │
           │                 PM 上下文 (核心)                   │    │
           │              (协作中心 / Collaboration Hub)        │    │
           │                                                    │    │
           │   ACL → HR OHS  │  ACL → CRM OHS  │ ACL → 组织OHS  │    │
           │   事件 ← CRM    │  事件 → HR                   │    │
           └────────────────────────────────────────────────────┘    │
                                                                │
    ┌───────────────────────────────────────────────────────────┘
    │
    ▼
┌─────────────────────────────────────────────────────────────────┐
│                         伴生系统                                  │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐                      │
│  │ OA系统   │  │ 薪资系统 │  │ 财务系统 │                      │
│  └──────────┘  └──────────┘  └──────────┘                      │
└─────────────────────────────────────────────────────────────────┘
```

## 映射关系详解

### 1. PM → HR (下游调用上游)

| 属性 | 说明 |
|-----|------|
| 关系类型 | 下游(PM) / 上游(HR) |
| 集成方式 | REST API (同步) + 消息队列 (异步) |
| PM端 | ACL防腐层，调用HR服务 |
| HR端 | OHS开放主机服务 |
| 数据流向 | HR → PM (员工信息、可用性) |
| 典型场景 | PM查询可用员工分配项目 |

**ACL设计 (PM端)**:

```java
// PM上下文 - HR防腐层
public class HrAclService {

    private final HrApiClient hrApiClient;

    public AvailableEmployeesDto getAvailableEmployees(
        DateRange period, SkillRequirement skills) {

        // 调用HR API
        HrApiResponse response = hrApiClient.getAvailableEmployees(period);

        // 数据转换 - 隔离HR数据结构变化
        return AvailableEmployeesDto.builder()
            .employees(response.getData().stream()
                .map(this::convertToPmEmployee)
                .collect(toList()))
            .build();
    }

    private PmEmployee convertToPmEmployee(HrEmployeeDto hrEmployee) {
        // 转换逻辑，保护PM领域模型不受HR变化影响
        return PmEmployee.builder()
            .id(hrEmployee.getId())
            .name(hrEmployee.getFullName())
            .skills(mapSkills(hrEmployee.getSkills()))
            .availability(calculateAvailability(hrEmployee))
            .build();
    }
}
```

### 2. PM → CRM (下游调用上游)

| 属性 | 说明 |
|-----|------|
| 关系类型 | 下游(PM) / 上游(CRM) |
| 集成方式 | REST API |
| PM端 | ACL防腐层 |
| CRM端 | OHS开放主机服务 |
| 数据流向 | CRM → PM (客户信息、合同信息) |
| 典型场景 | PM立项时关联客户合同 |

**ACL设计 (PM端)**:

```java
public class CrmAclService {

    private final CrmApiClient crmApiClient;

    public ContractInfo getContractInfo(String contractId) {
        CrmContractResponse response = crmApiClient.getContract(contractId);

        return ContractInfo.builder()
            .contractId(response.getId())
            .customerId(response.getCustomerId())
            .customerName(response.getCustomerName())
            .amount(response.getAmount())
            .build();
    }
}
```

### 3. CRM → PM (上游通知下游)

| 属性 | 说明 |
|-----|------|
| 关系类型 | 上游(CRM) / 下游(PM) |
| 集成方式 | 消息队列 (RabbitMQ) |
| CRM端 | 事件发布者 |
| PM端 | 事件消费者 |
| 数据流向 | CRM → PM (合同签订事件) |
| 典型场景 | 合同签订后触发PM项目立项 |

**事件契约**:

```java
// 共享事件契约 (eas-common-contracts)
public class ContractSignedEvent {

    private String eventId;
    private String contractId;
    private String customerId;
    private String customerName;
    private BigDecimal amount;
    private LocalDateTime signedAt;
    private String projectId; // 可选，如果已关联项目
}
```

**事件消费 (PM端)**:

```java
@Component
public class ContractSignedEventHandler {

    @RabbitListener(queues = "pm.contract.signed")
    public void handle(ContractSignedEvent event) {
        // 创建项目建议或关联现有项目
        projectSuggestionService.createSuggestionFromContract(event);
    }
}
```

### 4. 各上下文 → 组织上下文

| 属性 | 说明 |
|-----|------|
| 关系类型 | 下游(各上下文) / 上游(组织) |
| 集成方式 | REST API |
| 各端 | ACL防腐层 |
| 组织端 | OHS开放主机服务 |
| 典型场景 | 查询部门树、岗位列表 |

### 5. 各上下文 → 通知上下文

| 属性 | 说明 |
|-----|------|
| 关系类型 | 发布者(各上下文) / 订阅者(通知) |
| 集成方式 | 消息队列 |
| 典型场景 | 员工入职通知、任务分配通知 |

## 关系类型符号说明

| 符号 | 名称 | 说明 |
|-----|------|------|
| OHS | Open Host Service | 开放主机服务，提供标准化API |
| ACL | Anti-Corruption Layer | 防腐层，隔离外部依赖 |
| PL | Published Language | 发布语言，共享的数据格式 |
| U/D | Upstream/Downstream | 上游/下游关系 |

## PM作为协作中心

PM上下文是EAS系统的协作中心，原因：

1. **数据汇聚点**：PM需要HR的人员、CRM的客户合同、组织的架构信息
2. **业务协调者**：项目执行涉及跨部门协作
3. **事件枢纽**：接收CRM的合同事件，向HR发送人员释放事件

这种中心地位带来了架构考虑：

- **高可用性要求**：PM上下文是关键路径
- **性能优化**：PM的ACL调用需要考虑缓存策略
- **降级方案**：HR/CRM不可用时，PM需要有降级能力

---

**变更记录**
- 2026-05-27: 初始版本，张毅创建
