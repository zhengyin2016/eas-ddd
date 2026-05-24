# 融合设计文档 -- 培训上下文

> 本文档记录培训上下文融合设计的完整内容。
> 来源：《解构领域驱动设计》第 20.3.5 节
> 阶段：融合设计（Phase 4）

---

## 1. 资源库实现

### 1.1 技术选型：MyBatis

选择MyBatis作为ORM框架，采用配置方式定义Mapper。这一选择的核心考量是减少对资源库端口接口的侵入。

在菱形对称架构中，资源库接口属于南向网关的端口角色。端口接口表达的是领域对数据持久化的抽象需求，定义在`southbound/port/repository`包中。如果使用JPA的注解（如`@Entity`、`@Column`），就会将JPA的技术细节引入领域层，破坏领域模型的纯净性。

MyBatis的配置方式将SQL语句从Java代码中完全分离到XML文件中。领域层只引用端口接口，适配器层实现端口接口并通过Mapper XML定义具体的数据库操作。这种分离确保了领域模型不依赖任何持久化技术。

### 1.2 端口接口

资源库端口接口已通过测试驱动开发推导而出。在TDD过程中，编写领域服务测试时需要Mock资源库，由此反向推导出资源库接口的方法签名。

核心资源库端口接口包括：

- **TicketRepository**：培训票的持久化操作
- **TicketHistoryRepository**：培训票历史记录的持久化操作
- **CandidateRepository**：候选人的持久化操作
- **LearningRepository**：学习记录的持久化操作
- **ValidDateRepository**：有效日期的持久化操作

### 1.3 MyBatis Mapper XML示例

以`TicketHistoryMapper.xml`为例：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
    "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="com.eas.trainingcontext.southbound.adapter.repository.TicketHistoryRepositoryImpl">

    <resultMap id="ticketHistoryResultMap"
               type="com.eas.trainingcontext.domain.tickethistory.entity.TicketHistory">
        <id property="id" column="id"/>
        <result property="ticketId" column="ticket_id"/>
        <result property="operatorId" column="operator_id"/>
        <result property="operation" column="operation"/>
        <result property="operatedAt" column="operated_at"/>
    </resultMap>

    <select id="findByTicketId" resultMap="ticketHistoryResultMap">
        SELECT id, ticket_id, operator_id, operation, operated_at
        FROM training_ticket_history
        WHERE ticket_id = #{ticketId}
        ORDER BY operated_at ASC
    </select>

    <insert id="add" parameterType="com.eas.trainingcontext.domain.tickethistory.entity.TicketHistory">
        INSERT INTO training_ticket_history (id, ticket_id, operator_id, operation, operated_at)
        VALUES (#{id}, #{ticketId}, #{operatorId}, #{operation}, #{operatedAt})
    </insert>

</mapper>
```

### 1.4 集成测试

集成测试先于适配器实现编写。测试使用H2内存数据库，通过Flyway管理Schema，验证适配器是否正确地将领域操作翻译为数据库操作。

```java
@DataJpaTest
@MybatisTest
class TicketHistoryRepositoryImplTest {

    @Autowired
    private TicketHistoryRepository ticketHistoryRepository;

    @Test
    void should_find_history_by_ticket_id() {
        // given: 预置历史记录
        TicketHistory history = new TicketHistory(...);
        ticketHistoryRepository.add(history);

        // when: 按票ID查询
        List<TicketHistory> histories = ticketHistoryRepository.findByTicketId(history.getTicketId());

        // then: 返回正确的历史记录
        assertThat(histories).hasSize(1);
        assertThat(histories.get(0).getOperation()).isEqualTo("NOMINATE");
    }
}
```

---

## 2. 应用服务实现

### 2.1 服务契约

应用服务接口的方法参数和返回值都必须是消息契约对象（定义在`message`包中），不能直接使用领域模型。消息契约对象遵循JavaBean规范，支持JSON序列化。

核心消息契约对象：

| 契约对象 | 用途 |
|---------|------|
| `NominationRequest` | 提名请求参数（培训ID、候选人ID、协调者ID等） |
| `TicketResponse` | 培训票响应（票ID、状态、被提名人信息等） |
| `TrainingResponse` | 培训响应（培训ID、课程信息、状态等） |

### 2.2 依赖注入

Spring作为依赖注入框架，应用服务通过构造函数注入获取所需的领域服务和资源库端口接口。

```java
@Service
public class NominationAppService {

    private final NominationService nominationService;
    private final TicketRepository ticketRepository;
    private final CandidateRepository candidateRepository;

    public NominationAppService(NominationService nominationService,
                                 TicketRepository ticketRepository,
                                 CandidateRepository candidateRepository) {
        this.nominationService = nominationService;
        this.ticketRepository = ticketRepository;
        this.candidateRepository = candidateRepository;
    }
}
```

### 2.3 声明式事务

应用服务方法使用`@Transactional`注解标记事务边界。事务管理不应泄漏到领域层。

```java
@Transactional
public void nominate(NominationRequest request) {
    // 编排领域服务和资源库
}
```

### 2.4 异常分层

| 异常类型 | 层次 | 语义 | HTTP映射 |
|---------|------|------|---------|
| `DomainException` | 领域层 | 领域规则违反 | 400 Bad Request |
| `ApplicationException` | 应用层 | 业务流程错误 | 400 Bad Request |
| `Exception` | 基础设施 | 系统故障 | 500 Internal Server Error |

### 2.5 NominationAppService实现

```java
@Service
public class NominationAppService {

    private final NominationService nominationService;
    private final TicketRepository ticketRepository;
    private final CandidateRepository candidateRepository;

    public NominationAppService(NominationService nominationService,
                                 TicketRepository ticketRepository,
                                 CandidateRepository candidateRepository) {
        this.nominationService = nominationService;
        this.ticketRepository = ticketRepository;
        this.candidateRepository = candidateRepository;
    }

    @Transactional
    public TicketResponse nominate(NominationRequest request) {
        try {
            TicketId ticketId = new TicketId(request.getTicketId());
            CandidateId candidateId = new CandidateId(request.getCandidateId());
            NomineeId nomineeId = new NomineeId(request.getNomineeId());

            Ticket ticket = ticketRepository.findBy(ticketId);
            Candidate candidate = candidateRepository.findBy(candidateId);

            nominationService.nominate(ticket, candidate, nomineeId);

            ticketRepository.update(ticket);

            return TicketResponse.from(ticket);
        } catch (DomainException e) {
            throw new ApplicationException("提名操作失败: " + e.getMessage(), e);
        }
    }
}
```

### 2.6 集成测试

集成测试覆盖两个关键场景：

1. **正常执行路径**：验证从请求到响应的完整链路，包括数据库的持久化结果
2. **事务回滚路径**：模拟领域服务抛出异常，验证数据库回滚到操作前的状态

---

## 3. 远程服务实现

### 3.1 REST风格Resource

远程服务采用REST风格，使用Spring MVC注解。

```java
@RestController
@RequestMapping("/api/tickets")
public class TicketResource {

    private final NominationAppService nominationAppService;

    public TicketResource(NominationAppService nominationAppService) {
        this.nominationAppService = nominationAppService;
    }

    @PostMapping("/{ticketId}/nominate")
    public ResponseEntity<ResponseMessage> nominate(
            @PathVariable String ticketId,
            @RequestBody NominationRequest request) {
        request.setTicketId(ticketId);
        return Resources.execute(() ->
            nominationAppService.nominate(request), "提名候选人");
    }
}
```

### 3.2 Resources辅助类

`Resources`辅助类定义在共享内核`ddd-core`中，提供统一的异常处理和响应封装模板。

核心方法签名：

```java
public static ResponseEntity<ResponseMessage> execute(Supplier<Object> supplier, String description)
public static ResponseEntity<ResponseMessage> execute(Runnable runnable, String description)
```

异常处理策略：

| 异常类型 | 日志级别 | HTTP状态码 |
|---------|---------|-----------|
| `DomainException` | WARN | 400 Bad Request |
| `ApplicationException` | WARN | 400 Bad Request |
| `Exception` | ERROR | 500 Internal Server Error |

### 3.3 TicketResource端点

| HTTP方法 | 路径 | 说明 |
|---------|------|------|
| GET | `/api/tickets/{ticketId}` | 查询培训票详情 |
| POST | `/api/tickets/{ticketId}/nominate` | 提名候选人 |
| POST | `/api/tickets/{ticketId}/confirm` | 确认票 |
| POST | `/api/tickets/{ticketId}/cancel` | 取消票 |

### 3.4 TrainingResource端点

| HTTP方法 | 路径 | 说明 |
|---------|------|------|
| POST | `/api/trainings` | 创建培训 |
| POST | `/api/trainings/{trainingId}/checkin` | 发起签到 |
| POST | `/api/trainings/{trainingId}/confirm-absence` | 确认缺勤名单 |

---

## 4. 完整代码模型

### 4.1 代码模型总览（对应图20-61）

```
eas-ddd/                                    # 项目名称
├── ddd-core/                               # 共享内核
│   └── com.eas.dddcore/
│       ├── AggregateRoot.java
│       ├── Entity.java
│       ├── ValueObject.java
│       ├── Identity.java
│       ├── DomainEvent.java
│       ├── Repository.java
│       ├── DomainException.java
│       ├── ApplicationException.java
│       ├── Resources.java
│       └── ResponseMessage.java
│
├── eas-training-context/                   # 模块名 = 项目名-上下文名-context
│   └── com.eas.trainingcontext/            # 命名空间 = com.eas.上下文名context
│       ├── message/                        # 发布语言PL
│       │   ├── NominationRequest.java
│       │   ├── TicketResponse.java
│       │   └── TrainingResponse.java
│       │
│       ├── northbound/                     # 开放主机服务OHS
│       │   ├── appservice/                 # 应用服务
│       │   │   ├── NominationAppService.java
│       │   │   └── TrainingAppService.java
│       │   ├── remote/                     # 远程服务
│       │   │   ├── TicketResource.java
│       │   │   └── TrainingResource.java
│       │   └── local/                      # 本地服务
│       │
│       ├── domain/                         # 领域层（按聚合划分）
│       │   ├── ticket/                     # 培训票聚合
│       │   │   ├── entity/
│       │   │   ├── valueobject/
│       │   │   ├── service/
│       │   │   └── repository/
│       │   ├── tickethistory/              # 培训票历史聚合
│       │   ├── candidate/                  # 候选人聚合
│       │   ├── learning/                   # 学习聚合
│       │   ├── training/                   # 培训聚合
│       │   ├── course/                     # 课程聚合
│       │   ├── filter/                     # 筛选器聚合
│       │   ├── validdate/                  # 有效日期聚合
│       │   ├── validdateaction/            # 有效日期动作聚合
│       │   ├── cancellingaction/           # 取消动作聚合
│       │   ├── attendance/                 # 出勤聚合
│       │   └── blacklist/                  # 黑名单聚合
│       │
│       └── southbound/                     # 防腐层ACL
│           ├── port/                       # 端口（接口）
│           │   ├── repository/             # 资源库端口
│           │   └── client/                 # 客户端端口
│           └── adapter/                    # 适配器（实现）
│               ├── repository/             # 资源库适配器（MyBatis）
│               └── client/                 # 客户端适配器
│
└── eas-entry/                              # 唯一启动入口
    └── com.eas.entry/
        └── EasApplication.java
```

### 4.2 命名空间说明

本书使用的命名空间为 `eas.valueadded.trainingcontext`，其中 `valueadded` 表示业务价值层。本项目采用 `com.eas.trainingcontext`，遵循 Java 包命名的反域名惯例（reverse domain name convention）。两者在架构语义上等价，区别仅在于命名风格：

- **书中命名**：`eas.valueadded.trainingcontext` — 强调业务价值层分类
- **项目命名**：`com.eas.trainingcontext` — 遵循 Java 标准包命名规范

### 4.3 关键设计约束

1. **domain包按聚合划分**，而非按entity/valueobject/service分类
2. **依赖方向从外向内**：northbound/southbound依赖domain，domain不依赖任何外层
3. **端口与适配器分离**：domain层只引用southbound/port中的接口
4. **消息契约对象隔离**：跨层数据传输使用message包中的契约对象
5. **ddd-core单向依赖**：所有限界上下文依赖ddd-core，ddd-core不依赖任何限界上下文
6. **eas-entry唯一入口**：单体部署时的启动点，微服务拆分时每个上下文可独立启动
