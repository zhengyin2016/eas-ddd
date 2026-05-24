# EAS-DDD 企业应用套件 - 领域驱动设计实战项目

> 逆向还原张逸《解构领域驱动设计》第20.3章 EAS 案例，以作者第一视角完整模拟30天DDD开发过程

## 项目概述

EAS（Enterprise Application Suite）是面向"软件集团"的企业级应用软件，核心愿景是**实现人力资源的供需平衡**。

软件集团概况：300人规模，多个子公司，项目制管理。当前痛点：
- 人力资源调度全靠Excel，无法实时掌握人员分布和项目需求
- 客户关系管理混乱，商机流失严重
- 项目进度跟踪靠周会和Excel，信息滞后

## DDD参考过程模型（30天4阶段）

### 第一阶段：全局分析（Day 1-7）
识别利益相关者 → 商业模式画布 → 业务流程梳理 → 业务服务识别 → 子领域划分

### 第二阶段：架构映射（Day 8-14）
系统上下文 → V型映射识别限界上下文 → 菱形对称架构 → 上下文映射 → 统一语言

### 第三阶段：领域建模（Day 15-25）
快速建模法 → 庖丁解牛之聚合设计 → 服务驱动设计 → 测试驱动开发

### 第四阶段：集成测试与复盘（Day 26-30）
限界上下文集成 → 集成/系统/验收测试 → 文档完善 → 项目复盘

## 技术栈

- **语言**: Java 17
- **框架**: Spring Boot 3.2
- **ORM**: MyBatis 3.0.3
- **数据库**: MySQL 8.x
- **数据库迁移**: Flyway
- **测试**: JUnit 5 + Mockito
- **构建**: Maven

## 限界上下文与子领域

| 限界上下文 | Maven模块 | 子领域类型 | 核心聚合 |
|-----------|----------|-----------|---------|
| 人力资源上下文 | eas-hr-context | 核心 | 员工、储备人才、招聘、考勤、培训 |
| 项目管理上下文 | eas-pm-context | 核心 | 项目、问题、迭代、人员分配 |
| 客户关系管理上下文 | eas-crm-context | 核心 | 客户、商机、订单、合同 |
| 组织上下文 | eas-org-context | 通用 | 组织结构、部门 |
| 认证上下文 | eas-auth-context | 通用 | 用户、角色、权限 |
| 通知上下文 | eas-notification-context | 支撑 | 消息通知 |

## 菱形对称架构（每个核心限界上下文）

```
northbound/          # 北向网关（开放主机服务）
  remote/            # 远程服务（REST控制器）
  local/             # 本地服务（领域事件处理器）
  appservice/        # 应用服务（编排、事务）
domain/              # 领域层（纯净，无基础设施依赖）
  {aggregate}/       # 按聚合划分
    entity/          # 实体
    valueobject/     # 值对象
    service/         # 领域服务
    repository/      # 资源库接口（端口）
southbound/          # 南向网关（防腐层）
  port/              # 端口定义
    repository/      # 资源库端口
    client/          # 外部客户端端口
  adapter/           # 适配器实现
    repository/      # 资源库适配器（MyBatis Mapper）
    client/          # 外部客户端适配器
message/             # 消息契约对象（DTO）
```

## 团队角色

| 角色 | 姓名 | 分支 | 职责 |
|------|------|------|------|
| 总架构师/导师 | 张毅 | main | 架构决策、统一语言、代码合并、Vlog |
| 业务分析师 | 李华 | feature/business-analysis | 业务流程、服务规约 |
| HR领域专家 | 王强 | feature/domain-expert-hr | HR业务规则、领域评审 |
| PM领域专家 | 赵敏 | feature/domain-expert-pm | PM业务规则、领域评审 |
| 销售领域专家 | 孙磊 | feature/domain-expert-sales | CRM业务规则、领域评审 |
| 后端（HR） | 周涛 | feature/context-hr | HR限界上下文建模与实现 |
| 后端（PM） | 吴斌 | feature/context-pm | PM限界上下文建模与实现 |
| 后端（CRM） | 郑丽 | feature/context-crm | CRM限界上下文建模与实现 |
| 测试工程师 | 钱伟 | feature/testing | 集成测试、系统测试、验收测试 |

## 目录结构

```
eas-ddd/
├── pom.xml                    # Maven父POM
├── eas-common/                # 共享内核
├── eas-hr-context/            # 人力资源限界上下文
├── eas-pm-context/            # 项目管理限界上下文
├── eas-crm-context/           # 客户关系管理限界上下文
├── eas-org-context/           # 组织上下文（通用）
├── eas-auth-context/          # 认证上下文（通用）
├── eas-notification-context/  # 通知上下文（支撑）
├── eas-entry/                 # Spring Boot启动入口
└── docs/                      # 项目文档
    ├── vlog/                  # 每日Vlog日志
    ├── meetings/              # 会议记录
    ├── interviews/            # 访谈记录
    ├── global-analysis/       # 全局分析阶段工件
    ├── architecture-mapping/  # 架构映射阶段工件
    ├── domain-modeling/       # 领域建模阶段工件
    ├── business-service-specs/ # 业务服务规约
    ├── ubiquitous-language/    # 统一语言词典
    └── adr/                   # 架构决策记录
```

## Git工作流

1. 所有开发在功能分支进行，禁止直接提交main
2. 提交信息格式：`类型: 描述`（如 `feat: 添加员工创建业务服务`）
3. 阶段完成后提交PR，总架构师审查后合并
4. 合并后全员同步main最新代码

## 关键约束

1. **严格遵循DDD参考过程模型**，不跳步
2. **所有决策记录ADR**
3. **统一语言贯穿始终**
4. **代码严格遵循菱形对称架构**
5. **包含真实的团队讨论和争论**
6. **包含错误和修正过程**
7. **所有输出可执行、可验证**
8. **Vlog日志真实生动，第一人称"我"的口吻**
