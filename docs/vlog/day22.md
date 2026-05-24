# Day 22 - PM上下文代码实现：菱形架构落地

> 2026-05-24 | 张毅

---

今天是PM上下文的代码实现日。吴斌把前几天设计的模型全部翻译成了Java代码。

"菱形对称架构的好处是层次清晰，"吴斌说，"领域层在最里面，完全不依赖Spring。应用层在外面，用@Service注解。基础设施层在最外面，用@Repository和@Component。"

他先从领域层开始。

"Project是聚合根，用Builder模式构建，"吴斌说，"状态转换方法都封装在聚合根里，调用时检查状态机的前置条件。"

他展示了Project的代码：

```java
public class Project {
    public void approve() {
        if (this.status != ProjectStatus.PREPARING) {
            throw new IllegalStateException("只有准备中的项目才能审批");
        }
        this.status = ProjectStatus.APPROVED;
    }
}
```

"这样设计的好处是，状态机规则不会散落在各处，"我说，"所有规则都在聚合根里，一致性有保障。"

"对，而且测试很简单，"吴斌说，"直接测试Project对象就行了，不需要启动Spring容器。"

接下来是值对象。"ProjectStatus用枚举实现，ProjectId用record实现，"他说，"Java 17的record正好适合值对象——不可变、自动生成equals/hashCode/toString。"

"Task和Milestone是实体，"吴斌继续说，"它们属于Project聚合，所以没有独立的Repository，操作必须通过Project聚合根。"

"这个设计上周讨论过，"赵敏说，"Task不能独立存在，必须依附于Project。"

"对，"吴斌说，"如果想改Task的状态，必须先加载Project，然后通过project.getTask(taskId).start()来修改。这样Project可以检查整个聚合的不变性。"

Assignment是另一个聚合根。"Assignment是PM和HR之间的桥梁，"吴斌说，"它持有employeeId，但不持有Employee对象——我们只存ID，避免跨聚合的强耦合。"

"那怎么验证员工是否可用？"李华问。

"通过ACL，"吴斌展示了HRClientAdapter的代码，"AssignmentAppService调用AssignmentDomainService，AssignmentDomainService再调用HRClientPort（ACL），HRClientAdapter实现这个接口，调用HR的REST API。"

他展示了调用链：

```
AssignmentAppService.assignMember()
  → AssignmentDomainService.validateAndAssign()
    → HRClientAdapter.isEmployeeAvailable()  ← ACL
      → HR REST API
```

"这样设计的好处是，PM上下文不需要知道HR的领域模型，"我说，"HR的变化不会影响PM。"

"对，ACL做转换，"吴斌说，"HR返回的是Employee对象，我们转换成AvailableEmployeeDTO，只保留PM需要的数据。"

下午，吴斌开始实现基础设施层。

"MyBatis的Mapper接口和XML分开写，"他说，"接口在southbound/adapter/repository，XML在resources/mapper。"

他展示了ProjectMapper的XML：

```xml
<update id="update">
    UPDATE project SET ...
    WHERE id = #{id} AND version = #{version}
</update>
```

"这里用了乐观锁，"我说，"version字段做并发控制。"

"对，如果更新时version变了，更新会失败，Spring会抛出OptimisticLockingFailureException，"吴斌说，"应用层可以捕获这个异常，提示用户重试。"

然后是应用服务层。"ProjectAppService是用例的直接实现，"吴斌说，"它不包含业务逻辑，只是协调——加载领域对象、调用领域方法、保存结果、发布事件。"

他展示了ProjectAppService的代码：

```java
@Service
public class ProjectAppService {
    @Transactional
    public void approveProject(String projectId) {
        Project project = findProject(projectId);
        project.approve();
        projectRepository.save(project);
        // TODO: 发布ProjectApprovedEvent
    }
}
```

"事务边界在应用服务，"我说，"领域方法不直接操作数据库，只是修改内存状态。"

"对，"吴斌说，"领域层保持纯净，不依赖任何框架。"

最后是REST控制器。"ProjectResource是北向网关，"他说，"它接收HTTP请求，调用应用服务，返回响应。"

他展示了ProjectResource的代码：

```java
@RestController
@RequestMapping("/api/pm/projects")
public class ProjectResource {
    @PostMapping("/{id}/approve")
    public void approveProject(@PathVariable String id) {
        projectAppService.approveProject(id);
    }
}
```

"这样设计的好处是，API可以独立变化，"我说，"比如以后要加GraphQL，只需要加新的Controller，应用服务不用变。"

下午四点，代码基本完成了。吴斌运行了单元测试，全部通过。

"测试覆盖率不错，"他说，"领域层的测试最密集，因为这是核心逻辑。"

"让我看看，"我接过电脑，翻看测试代码，"ProjectTest测试了状态机、任务创建、里程碑管理；AssignmentTest测试了分配比例验证、释放逻辑；IssueTest测试了问题生命周期。这些都覆盖了核心业务规则。"

"我还加了边界条件测试，"吴斌说，"比如已关闭的项目不能创建任务，分配比例必须在0-100之间。"

"很好，"我说，"这些边界条件是bug的高发区，测试覆盖到了就放心了。"

接下来是数据库迁移。"Flyway脚本从V10开始，"吴斌说，"避免和HR上下文的V1-V9冲突。"

他展示了几个SQL脚本：

```sql
-- V10: 创建project表
CREATE TABLE project (
    id VARCHAR(36) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PREPARING',
    version INT NOT NULL DEFAULT 0,
    ...
);

-- V11: 创建assignment表
CREATE TABLE assignment (
    id VARCHAR(36) PRIMARY KEY,
    project_id VARCHAR(36) NOT NULL,
    employee_id VARCHAR(36) NOT NULL,
    ...
    FOREIGN KEY (project_id) REFERENCES project(id)
);
```

"级联删除对吗？"李华问，"删除Project会级联删除Assignment？"

"对，"吴斌说，"因为Assignment不能脱离Project存在。如果不想级联删除，要先手动释放所有Assignment。"

"这个设计合理，"我说，"项目删除了，分配也没意义了。"

五点的时候，所有代码都完成了。吴斌提交了代码，推送到GitHub。

"PM上下文的菱形架构落地了，"他说，"领域层、应用层、基础设施层，边界清晰。"

"ACL也实现了，"我说，"PM调用HR通过HRClientAdapter，防腐层保护了PM上下文。"

赵敏说："我下午看了一遍代码，感觉聚合的职责很清晰。Project管项目生命周期，Assignment管人员分配，Issue管问题跟踪。各司其职，不会互相干扰。"

"这就是DDD的好处，"我说，"聚合划清楚了，代码组织就清晰了。"

**明天计划**：开始CRM上下文的领域分析——客户、合同、商机。

散会时，吴斌说："今天的代码实现挺顺利的，主要是前面的设计工作做得充分。"

"设计充分，编码就快，"我说，"这是DDD的实践心得。"

Day 22，完成。

## PM上下文代码清单

### 领域层
- com.eas.pm.domain.project
  - Project.java (聚合根)
  - ProjectId.java (值对象)
  - ProjectStatus.java (值对象/枚举)
  - Task.java (实体)
  - TaskId.java (值对象)
  - TaskStatus.java (值对象/枚举)
  - Milestone.java (实体)
  - MilestoneId.java (值对象)
  - ProjectRepository.java (端口接口)
  - service/ProjectDomainService.java (领域服务)

- com.eas.pm.domain.assignment
  - Assignment.java (聚合根)
  - AssignmentId.java (值对象)
  - AssignmentRole.java (值对象/枚举)
  - AssignmentRepository.java (端口接口)
  - service/AssignmentDomainService.java (领域服务)

- com.eas.pm.domain.issue
  - Issue.java (聚合根)
  - IssueId.java (值对象)
  - IssueStatus.java (值对象/枚举)
  - IssueSeverity.java (值对象/枚举)
  - IssueRepository.java (端口接口)

- com.eas.pm.domain.iteration
  - Iteration.java (聚合根)
  - IterationId.java (值对象)
  - IterationStatus.java (值对象/枚举)
  - IterationRepository.java (端口接口)

### 应用层
- com.eas.pm.northbound.appservice
  - ProjectAppService.java
  - AssignmentAppService.java
  - IssueAppService.java

### 北向网关（REST）
- com.eas.pm.northbound.remote
  - ProjectResource.java
  - AssignmentResource.java
  - IssueResource.java

### 南向网关（基础设施）
- com.eas.pm.southbound.port.client
  - HRClientPort.java (ACL端口)

- com.eas.pm.southbound.adapter.client
  - HRClientAdapter.java (ACL适配器)

- com.eas.pm.southbound.adapter.repository
  - ProjectRepositoryImpl.java
  - ProjectMapper.java + ProjectMapper.xml
  - AssignmentRepositoryImpl.java
  - AssignmentMapper.java + AssignmentMapper.xml

### 消息契约
- com.eas.pm.message
  - CreateProjectRequest.java
  - ProjectResponse.java
  - AssignMemberRequest.java
  - CreateTaskRequest.java
  - CreateIssueRequest.java
  - AvailableEmployeeDTO.java

### 数据库迁移
- V10__create_project_table.sql
- V11__create_assignment_table.sql
- V12__create_task_table.sql
- V13__create_issue_table.sql
- V14__create_iteration_table.sql
- V15__create_milestone_table.sql

### 单元测试
- ProjectTest.java (状态流转、任务创建、里程碑管理)
- AssignmentTest.java (分配/释放、比例验证)
- IssueTest.java (问题生命周期)
- TaskTest.java (任务状态流转)
