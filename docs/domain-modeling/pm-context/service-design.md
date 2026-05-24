# PM上下文服务驱动设计

> 设计日期：2026-05-24
> 设计师：吴斌、张毅、赵敏

## 服务层次架构

```
┌─────────────────────────────────────────────────────────┐
│                     北向网关层                            │
│  ┌──────────────────────────────────────────────────┐   │
│  │ Remote (REST Controller)                         │   │
│  │   - ProjectResource                              │   │
│  │   - IssueResource                                │   │
│  │   - AssignmentResource                           │   │
│  └──────────────────────────────────────────────────┘   │
│  ┌──────────────────────────────────────────────────┐   │
│  │ AppService (Application Service)                 │   │
│  │   - ProjectAppService                            │   │
│  │   - AssignmentAppService                         │   │
│  │   - IssueAppService                              │   │
│  └──────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────┘
                            │
                            ▼
┌─────────────────────────────────────────────────────────┐
│                     领域层                               │
│  ┌──────────────────────────────────────────────────┐   │
│  │ Aggregate Root                                    │   │
│  │   - Project, Assignment, Issue, Iteration        │   │
│  └──────────────────────────────────────────────────┘   │
│  ┌──────────────────────────────────────────────────┐   │
│  │ Domain Service                                    │   │
│  │   - ProjectDomainService                         │   │
│  │   - AssignmentDomainService                      │   │
│  │   - ResourceQueryService                         │   │
│  └──────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────┘
                            │
                            ▼
┌─────────────────────────────────────────────────────────┐
│                     南向网关层                            │
│  ┌──────────────────────────────────────────────────┐   │
│  │ Repository Port (Interface)                      │   │
│  │   - ProjectRepository                            │   │
│  │   - AssignmentRepository                         │   │
│  │   - IssueRepository                              │   │
│  │   - IterationRepository                          │   │
│  └──────────────────────────────────────────────────┘   │
│  ┌──────────────────────────────────────────────────┐   │
│  │ Client Port (Interface) - ACL                    │   │
│  │   - HRClientPort                                 │   │
│  └──────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────┘
                            │
                            ▼
┌─────────────────────────────────────────────────────────┐
│                     基础设施适配器                        │
│  ┌──────────────────────────────────────────────────┐   │
│  │ Repository Adapter (Implementation)              │   │
│  │   - ProjectRepositoryImpl + MyBatis              │   │
│  │   - AssignmentRepositoryImpl + MyBatis           │   │
│  └──────────────────────────────────────────────────┘   │
│  ┌──────────────────────────────────────────────────┐   │
│  │ Client Adapter (ACL)                             │   │
│  │   - HRClientAdapter (调用HR REST API)            │   │
│  └──────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────┘
```

## 应用服务设计

### ProjectAppService

| 方法 | 用例 | 协调逻辑 |
|-----|------|---------|
| createProject(request) | 创建项目 | 1. 构建Project对象<br>2. 调用projectRepository.save()<br>3. 发布ProjectCreatedEvent |
| approveProject(projectId) | 审批项目 | 1. 加载Project<br>2. 调用project.approve()<br>3. 保存Project<br>4. 发布ProjectApprovedEvent |
| startProject(projectId) | 启动项目 | 1. 加载Project<br>2. 调用project.start()<br>3. 保存Project |
| suspendProject(projectId) | 暂停项目 | 1. 加载Project<br>2. 调用project.suspend()<br>3. 保存Project |
| resumeProject(projectId) | 恢复项目 | 1. 加载Project<br>2. 调用project.resume()<br>3. 保存Project |
| closeProject(projectId) | 结项 | 1. 加载Project<br>2. 调用project.close()<br>3. 保存Project<br>4. 发布ProjectClosedEvent（通知HR释放人员） |
| createTask(request) | 创建任务 | 1. 加载Project<br>2. 调用project.createTask()<br>3. 保存Project |
| startTask(taskId) | 开始任务 | 1. 加载Project<br>2. 调用project.getTask().start()<br>3. 保存Project |
| completeTask(request) | 完成任务 | 1. 加载Project<br>2. 调用project.getTask().complete(hours)<br>3. 保存Project |

### AssignmentAppService

| 方法 | 用例 | 协调逻辑 |
|-----|------|---------|
| assignMember(request) | 分配成员 | 1. 调用assignmentDomainService.validateAndAssign()<br>2. 构建Assignment对象<br>3. 保存Assignment |
| releaseMember(assignmentId) | 释放成员 | 1. 加载Assignment<br>2. 调用assignment.release()<br>3. 保存Assignment |
| updateAllocation(request) | 更新分配比例 | 1. 加载Assignment<br>2. 调用assignment.updateAllocation()<br>3. 保存Assignment |
| queryAvailableResources(projectId) | 查询可用资源 | 1. 加载Project获取时间段<br>2. 调用resourceQueryService.queryAvailable()<br>3. 返回结果列表 |

### IssueAppService

| 方法 | 用例 | 协调逻辑 |
|-----|------|---------|
| createIssue(request) | 创建问题 | 1. 构建Issue对象<br>2. 调用issueRepository.save()<br>3. 发布IssueCreatedEvent |
| assignIssue(issueId, assigneeId) | 分配问题 | 1. 加载Issue<br>2. 调用issue.assign()<br>3. 保存Issue |
| resolveIssue(request) | 解决问题 | 1. 加载Issue<br>2. 调用issue.resolve()<br>3. 保存Issue |
| closeIssue(issueId) | 关闭问题 | 1. 加载Issue<br>2. 调用issue.close()<br>3. 保存Issue |

## 领域服务设计

### ProjectDomainService

| 方法 | 职责 |
|-----|------|
| validateProjectDates(startDate, endDate) | 验证项目日期合法性（结束日期不早于开始日期） |
| checkProjectBudget(budget) | 检查项目预算是否合法（非负数） |

### AssignmentDomainService

| 方法 | 职责 | 依赖 |
|-----|------|------|
| validateAndAssign(request) | 验证并分配员工 | HRClientPort（ACL） |
| checkAllocationConflict(employeeId, projectId, startDate, endDate) | 检查分配时间冲突 | AssignmentRepository |
| updateAssignmentsOnProjectDateChange(projectId, newStartDate, newEndDate) | 项目日期变化时更新受影响的分配 | AssignmentRepository |

### ResourceQueryService

| 方法 | 职责 |
|-----|------|
| queryProjectAvailableResources(projectId) | 查询项目可用资源（组合Project、Assignment、HR数据） |

## ACL防腐层设计

### HRClientPort（端口接口）

```java
package com.eas.pm.southbound.port.client;

import java.time.LocalDate;
import java.util.List;

/**
 * PM调用HR上下文的防腐层端口
 */
public interface HRClientPort {

    /**
     * 查询单个员工信息
     */
    AvailableEmployeeDTO queryEmployee(String employeeId);

    /**
     * 查询可用员工列表
     */
    List<AvailableEmployeeDTO> queryAvailableEmployees(LocalDate startDate, LocalDate endDate);

    /**
     * 检查员工是否可用
     */
    boolean isEmployeeAvailable(String employeeId, LocalDate startDate, LocalDate endDate);
}
```

### AvailableEmployeeDTO（PM的DTO）

```java
package com.eas.pm.message;

import java.math.BigDecimal;

/**
 * PM上下文对可用员工的DTO表示
 * 注意：这是PM的DTO，不是HR的领域模型
 */
public record AvailableEmployeeDTO(
    String employeeId,
    String name,
    BigDecimal availableRatio,
    String department
) {}
```

### HRClientAdapter（适配器实现）

```java
package com.eas.pm.southbound.adapter.client;

import com.eas.pm.message.AvailableEmployeeDTO;
import com.eas.pm.southbound.port.client.HRClientPort;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.List;

/**
 * HR上下文ACL适配器实现
 * 职责：调用HR REST API，将HR响应转换为PM DTO
 */
public class HRClientAdapter implements HRClientPort {

    private final RestTemplate restTemplate;
    private final String hrBaseUrl;

    public HRClientAdapter(RestTemplate restTemplate, String hrBaseUrl) {
        this.restTemplate = restTemplate;
        this.hrBaseUrl = hrBaseUrl;
    }

    @Override
    public AvailableEmployeeDTO queryEmployee(String employeeId) {
        // 调用HR API: GET /api/hr/employees/{id}
        HREmployeeResponse hrResponse = restTemplate.getForObject(
            hrBaseUrl + "/api/hr/employees/" + employeeId,
            HREmployeeResponse.class
        );
        // 转换为PM DTO
        return new AvailableEmployeeDTO(
            hrResponse.id(),
            hrResponse.name(),
            hrResponse.availableRatio(),
            hrResponse.department()
        );
    }

    @Override
    public List<AvailableEmployeeDTO> queryAvailableEmployees(LocalDate startDate, LocalDate endDate) {
        // 调用HR API: GET /api/hr/employees/available?start={start}&end={end}
        // ...
    }

    @Override
    public boolean isEmployeeAvailable(String employeeId, LocalDate startDate, LocalDate endDate) {
        // 调用HR API验证可用性
        // ...
    }
}
```

## 跨上下文事件设计

### PM发布的事件

| 事件 | 触发条件 | 订阅者 |
|-----|---------|--------|
| ProjectCreatedEvent | 项目创建 | - |
| ProjectApprovedEvent | 项目审批通过 | - |
| ProjectClosedEvent | 项目结项 | HR上下文（释放人员） |
| IssueCreatedEvent | 问题创建 | - |
| IssueAssignedEvent | 问题分配 | - |

### PM订阅的事件

| 事件 | 发布者 | PM响应 |
|-----|--------|--------|
| ContractSignedEvent | CRM上下文 | 触发项目立项流程 |

## 事务边界设计

| 应用服务 | 事务边界 | 说明 |
|---------|---------|------|
| createProject() | 单事务 | Project创建 |
| assignMember() | 单事务 | Assignment创建 + HR可用性验证（只读） |
| closeProject() | 两阶段提交 | Project更新 + 发布事件（最终一致性） |
| queryAvailableResources() | 无事务 | 只读查询 |

## 查询服务设计

### CQRS策略

| 查询 | 实现方式 |
|-----|---------|
| 查询项目详情 | 直接查询Project聚合 |
| 查询项目进度 | 组合Project、Task、Assignment数据 |
| 查询可用资源 | 跨Project、Assignment、HR上下文查询 |

### ResourceQueryService实现策略

由于需要跨上下文查询，采用以下策略：
1. 先查询Project获取时间段
2. 查询当前Assignment获取已分配人员
3. 调用HR ACL获取可用人员
4. 组合结果返回（允许最终一致性）
