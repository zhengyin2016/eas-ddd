# EAS-DDD 企业应用系统

## 项目说明

本项目严格按照张逸《解构领域驱动设计》第20.3章的EAS案例代码模型（图20-61）构建，采用领域驱动设计的战略模式与战术模式指导系统架构。

## 限界上下文划分（12个）

### 核心域（Core Domain）- 8个限界上下文
| 上下文 | 模块名 | 职责 |
|--------|--------|------|
| 订单上下文 | eas-order-context | 订单管理 |
| 合同上下文 | eas-contract-context | 合同管理 |
| 员工上下文 | eas-employee-context | 员工管理 |
| 人才上下文 | eas-talent-context | 人才管理 |
| 招聘上下文 | eas-recruitment-context | 招聘管理 |
| 项目上下文 | eas-project-context | 项目管理 |
| 培训上下文 | eas-training-context | 培训管理 |
| 分析上下文 | eas-analytics-context | 数据分析 |

### 支撑域（Supporting Domain）- 2个限界上下文
| 上下文 | 模块名 | 职责 |
|--------|--------|------|
| 文件上下文 | eas-file-context | 文件存储与管理 |
| OA协作上下文 | eas-oa-context | OA协作与消息队列 |

### 通用域（Generic Domain）- 2个限界上下文
| 上下文 | 模块名 | 职责 |
|--------|--------|------|
| 组织上下文 | eas-org-context | 组织架构管理 |
| 认证上下文 | eas-auth-context | 身份认证与授权 |

## 菱形对称架构

每个限界上下文内部采用菱形对称架构：

```
com.eas.{context}/
├── message/                          # 领域事件消息定义
├── northbound/                       # 北向（上游）
│   ├── remote/                       #   远程服务（REST Resource）
│   ├── local/                        #   本地服务（内部调用接口）
│   └── appservice/                   #   应用服务（编排用例）
├── domain/                           # 领域层
│   └── {aggregate}/                  #   聚合
│       ├── entity/                   #     实体
│       ├── valueobject/              #     值对象
│       ├── service/                  #     领域服务
│       └── repository/              #     资源库接口
└── southbound/                       # 南向（下游）
    ├── port/                         #   端口（接口）
    │   ├── repository/               #     资源库端口
    │   └── client/                   #     客户端端口
    └── adapter/                      #   适配器（实现）
        ├── repository/               #     资源库适配器（MyBatis）
        └── client/                   #     客户端适配器（Feign等）
```

## 培训上下文聚合（12个）

培训上下文（eas-training-context）是书中最完整的示例，包含以下聚合：
1. Training - 培训
2. Course - 课程
3. Learning - 学习
4. Ticket - 培训票
5. TicketHistory - 培训票历史
6. Filter - 筛选器
7. ValidDate - 有效日期
8. ValidDateAction - 有效日期动作
9. CancellingAction - 取消动作
10. Candidate - 候选人
11. Attendance - 出勤
12. Blacklist - 黑名单

## 技术栈

| 技术 | 版本 | 用途 |
|------|------|------|
| Java | 17 | 开发语言 |
| Spring Boot | 3.2.0 | 应用框架 |
| MyBatis | 3.0.3 | ORM框架 |
| MySQL | - | 关系数据库 |
| Flyway | 10.4.1 | 数据库迁移 |
| Spring Security | - | 认证授权 |
| RabbitMQ | - | 消息队列 |

## 共享内核（ddd-core）

提供领域驱动设计的基础抽象：
- `AggregateRoot<ID>` - 聚合根基类，管理领域事件
- `Entity<ID>` - 实体基类，通过标识判等
- `ValueObject` - 值对象标记接口
- `DomainEvent` - 领域事件基类
- `Identity` - 标识值对象（Java Record）
- `Repository<T, ID>` - 资源库标记接口
- `DomainException` - 领域异常
- `ApplicationException` - 应用层异常
- `Resources` - REST辅助类，统一响应格式
- `ResponseMessage` - 统一响应消息对象

## 模块依赖关系

```
ddd-core（共享内核）
  ↑
  ├── 8个核心上下文（order, contract, employee, talent, recruitment, project, training, analytics）
  ├── 2个支撑上下文（file, oa）
  └── 2个通用上下文（org, auth）
      ↑
    eas-entry（启动入口，聚合所有模块）
```

## 构建与运行

```bash
# 构建
mvn clean package

# 运行
java -jar eas-entry/target/eas-entry-1.0.0-SNAPSHOT.jar
```
