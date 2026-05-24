# 菱形对称架构设计 (Diamond Architecture)

**编号**: EAS-ARCH-003
**版本**: 1.0
**日期**: 2026-05-26
**作者**: 张毅

## 概述

本文档定义EAS系统的菱形对称架构，每个限界上下文采用统一的四层结构。

## 架构总览

```
        ╔═══════════════════════════════════════════════════════════════╗
        ║                    限界上下文 (Bounded Context)              ║
        ╠═══════════════════════════════════════════════════════════════╣
        ║                                                               ║
        ║     ┌─────────────────────────────────────────────────┐      ║
        ║     │           北向网关 (Northbound Gateway)          │      ║
        ║     │            开放主机服务 (OHS)                    │      ║
        ║     │  ┌─────────┐  ┌─────────┐  ┌─────────┐         │      ║
        ║     │  │ REST API│  │ API Doc │  │ Events  │         │      ║
        ║     │  └─────────┘  └─────────┘  └─────────┘         │      ║
        ║     └─────────────────────────────────────────────────┘      ║
        ║                          ▲                                  ║
        ║                          │                                  ║
        ║     ┌─────────────────────────────────────────────────┐      ║
        ║     │              应用层 (Application)                │      ║
        ║     │  ┌─────────┐  ┌─────────┐  ┌─────────┐         │      ║
        ║     │  │ Service │  │ Command │  │  Query  │         │      ║
        ║     │  └─────────┘  └─────────┘  └─────────┘         │      ║
        ║     └─────────────────────────────────────────────────┘      ║
        ║                          ▲                                  ║
        ║                          │                                  ║
        ║     ┌─────────────────────────────────────────────────┐      ║
        ║     │              领域层 (Domain)                     │      ║
        ║     │  ┌─────────┐  ┌─────────┐  ┌─────────┐         │      ║
        ║     │  │Aggregate│  │ Domain  │  │ Domain  │         │      ║
        ║     │  │  Root   │  │ Service │  │ Event   │         │      ║
        ║     │  └─────────┘  └─────────┘  └─────────┘         │      ║
        ║     │  ┌─────────┐  ┌─────────┐                       │      ║
        ║     │  │ Repository│ │ Factory │                       │      ║
        ║     │  │ Interface│  └─────────┘                       │      ║
        ║     │  └─────────┘                                    │      ║
        ║     └─────────────────────────────────────────────────┘      ║
        ║                          ▲                                  ║
        ║                          │                                  ║
        ║     ┌─────────────────────────────────────────────────┐      ║
        ║     │           南向网关 (Southbound Gateway)          │      ║
        ║     │             防腐层 (ACL)                         │      ║
        ║     │  ┌─────────┐  ┌─────────┐  ┌─────────┐         │      ║
        ║     │  │External │  │  Event  │  │  DB     │         │      ║
        ║     │  │  API    │  │Adapter  │  │ Adapter │         │      ║
        ║     │  │ Client  │  └─────────┘  └─────────┘         │      ║
        ║     │  └─────────┘                                    │      ║
        ║     └─────────────────────────────────────────────────┘      ║
        ║                                                               ║
        ╚═══════════════════════════════════════════════════════════════╝
                                    │
        ────────────────────────────┼────────────────────────────────────
                                    ▼
        ╔═══════════════════════════════════════════════════════════════╗
        ║                    基础设施层 (Infrastructure)               ║
        ║  MySQL | RabbitMQ | Redis | External Services                ║
        ╚═══════════════════════════════════════════════════════════════╝
```

## 北向网关 (开放主机服务 OHS)

### 职责

- 定义对外API契约
- 验证请求参数
- 转换DTO与领域对象
- 发布领域事件

### 组件

| 组件 | 说明 |
|-----|------|
| REST Controller | 暴露RESTful API |
| DTO | 数据传输对象，与领域模型分离 |
| OpenAPI文档 | 自动生成API文档 |
| Event Publisher | 发布领域事件到消息队列 |

### 代码结构

```
com.eas.{context}.adapter.in

├── controller          # REST控制器
│   └── EmployeeController.java
├── dto                # 请求/响应DTO
│   ├── request
│   │   └── CreateEmployeeRequest.java
│   └── response
│       └── EmployeeResponse.java
├── assembler          # DTO与领域对象转换
│   └── EmployeeAssembler.java
└── event              # 事件发布
    └── EmployeeEventPublisher.java
```

## 领域层 (Domain Layer)

### 职责

- 核心业务逻辑
- 领域模型定义
- 业务规则校验
- 领域事件生成

### 组件

| 组件 | 说明 |
|-----|------|
| Aggregate | 聚合根，实体，值对象 |
| Domain Service | 领域服务，跨聚合业务逻辑 |
| Repository接口 | 仓储接口，由基础设施实现 |
| Domain Event | 领域事件 |
| Factory | 工厂，复杂对象创建 |

### 代码结构

```
com.eas.{context}.domain

├── model              # 领域模型
│   ├── aggregate      # 聚合根
│   │   └── Employee.java
│   ├── entity         # 实体
│   │   └── ContactInfo.java
│   └── valueobject    # 值对象
│       ├── EmployeeId.java
│       └── EmployeeStatus.java
├── service            # 领域服务
│   └── EmployeeDomainService.java
├── repository         # 仓储接口
│   └── EmployeeRepository.java
├── event              # 领域事件
│   └── EmployeeCreatedEvent.java
└── factory            # 工厂
    └── EmployeeFactory.java
```

## 南向网关 (防腐层 ACL)

### 职责

- 调用外部上下文API
- 数据格式转换
- 异常处理与重试
- 隔离外部变化

### 组件

| 组件 | 说明 |
|-----|------|
| External API Client | 调用外部上下文的HTTP客户端 |
| Event Adapter | 消费外部事件并转换为领域操作 |
| DB Adapter | 数据库访问实现(Repository接口实现) |

### 代码结构

```
com.eas.{context}.adapter.out

├── external           # 外部API调用
│   ├── hr            # HR上下文客户端
│   │   └── HrApiClient.java
│   ├── crm           # CRM上下文客户端
│   │   └── CrmApiClient.java
│   └── fallback      # 降级策略
│       └── HrApiFallback.java
├── event             # 事件消费
│   └── EventConsumer.java
├── persist           # 持久化
│   ├── mapper        # MyBatis Mapper
│   │   └── EmployeeMapper.java
│   ├── po            # 持久化对象
│   │   └── EmployeePO.java
│   └── repository    # Repository实现
│       └── EmployeeRepositoryImpl.java
└── converter         # 数据转换
    └── EmployeeConverter.java
```

## 消息契约层 (Message Contracts)

### 职责

- 定义跨上下文事件契约
- 统一事件格式
- 事件版本管理

### 代码结构

```
com.eas.common.contracts

├── event
│   ├── hr
│   │   ├── EmployeeCreatedEvent.java
│   │   └── EmployeeResignedEvent.java
│   ├── pm
│   │   └── ProjectCreatedEvent.java
│   └── crm
│       └── ContractSignedEvent.java
└── dto                # 共享DTO
    ├── EmployeeDto.java
    └── OrganizationDto.java
```

## 各上下文的聚合列表

### HR上下文聚合

| 聚合 | 根实体 | 主要字段 | Repository |
|-----|--------|---------|-----------|
| 员工 | Employee | id, name, status, deptId, positionId | EmployeeRepository |
| 招聘需求 | RecruitmentRequirement | id, position, count, urgency | RecruitmentRequirementRepository |
| 面试 | Interview | id, candidateId, requirementId, status | InterviewRepository |
| 考勤 | Attendance | id, employeeId, month, workDays | AttendanceRepository |
| 培训 | Training | id, title, capacity, schedule | TrainingRepository |
| 技能 | Skill | id, employeeId, skillName, level | SkillRepository |

### PM上下文聚合

| 聚合 | 根实体 | 主要字段 | Repository |
|-----|--------|---------|-----------|
| 项目 | Project | id, name, status, customerId, managerId | ProjectRepository |
| 任务 | Task | id, projectId, title, status, assigneeId | TaskRepository |
| Issue | Issue | id, projectId, title, priority, status | IssueRepository |
| 人员分配 | ProjectMember | id, projectId, employeeId, role, fteRatio | ProjectMemberRepository |

### CRM上下文聚合

| 聚合 | 根实体 | 主要字段 | Repository |
|-----|--------|---------|-----------|
| 客户 | Customer | id, name, level, industry | CustomerRepository |
| 商机 | Opportunity | id, customerId, amount, stage | OpportunityRepository |
| 合同 | Contract | id, customerId, projectId, amount | ContractRepository |
| 回款 | Payment | id, contractId, amount, dueDate | PaymentRepository |

## 技术选型

| 层级 | 技术选型 | 版本 | 说明 |
|-----|---------|------|------|
| 应用框架 | Spring Boot | 3.2.0 | Web框架 |
| ORM | MyBatis Spring | 3.0.3 | SQL映射 |
| 数据库 | MySQL | 8.0 | 关系数据库 |
| 迁移 | Flyway | 9.22 | 数据库版本控制 |
| 消息队列 | RabbitMQ | 3.12 | 事件总线 |
| API文档 | SpringDoc | 2.2 | OpenAPI 3.0 |
| HTTP客户端 | WebClient | - | 响应式HTTP客户端 |

## 分层调用规则

1. **领域层**：可被应用层调用，不依赖任何外层
2. **应用层**：协调领域层，调用南向网关
3. **北向网关**：调用应用层，不直接调用领域层
4. **南向网关**：实现领域层定义的接口

---

**变更记录**
- 2026-05-26: 初始版本，张毅创建
