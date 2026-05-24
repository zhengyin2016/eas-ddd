# HR服务驱动设计文档

## 1. 业务服务到领域行为映射

| 业务服务 | 应用服务 | 任务分解 | 涉及聚合 | 领域行为 |
|---------|---------|---------|---------|---------|
| 创建员工 | EmployeeAppService.createEmployee() | 1.验证身份证唯一性<br>2.验证手机唯一性<br>3.创建Employee<br>4.保存<br>5.发布事件 | Employee | EmployeeDomainService.validateIdCard()<br>EmployeeDomainService.validatePhone()<br>Employee.create() |
| 分配组织 | EmployeeAppService.assignDepartment() | 1.验证员工存在<br>2.验证部门存在<br>3.更新部门<br>4.保存 | Employee | Employee.assignDepartment() |
| 办理离职 | EmployeeAppService.resign() | 1.验证员工存在<br>2.调用离职<br>3.保存<br>4.发布事件 | Employee | Employee.resign() |
| 办理调岗 | EmployeeAppService.transfer() | 1.验证员工存在<br>2.验证部门存在<br>3.调用调岗<br>4.保存<br>5.发布事件 | Employee | Employee.startTransfer()<br>Employee.completeTransfer() |
| 提交招聘需求 | RecruitmentAppService.submitRequirement() | 1.验证部门存在<br>2.创建招聘需求<br>3.保存<br>4.发布事件 | RecruitmentRequirement | RecruitmentRequirement.submit() |
| 安排面试 | RecruitmentAppService.scheduleInterview() | 1.验证招聘需求存在<br>2.安排面试<br>3.保存 | RecruitmentRequirement<br>Interview | RecruitmentRequirement.scheduleInterview() |
| 记录考勤 | AttendanceAppService.recordAttendance() | 1.验证员工存在<br>2.记录签到/签退<br>3.保存 | AttendanceRecord | AttendanceRecord.checkIn()<br>AttendanceRecord.checkOut() |
| 创建培训计划 | TrainingAppService.createPlan() | 1.创建培训计划<br>2.保存<br>3.发布事件 | TrainingPlan | TrainingPlan.create() |
| 培训报名 | TrainingAppService.enroll() | 1.验证培训存在<br>2.验证容量<br>3.报名<br>4.保存 | TrainingPlan<br>TrainingEnrollment | TrainingPlan.enroll() |
| 维护技能档案 | EmployeeAppService.addSkill() | 1.验证员工存在<br>2.添加技能<br>3.保存 | Employee | Employee.addSkill() |
| 管理储备人才 | TalentAppService.createTalent() | 1.创建储备人才<br>2.保存 | Talent | Talent.create() |
| 查询可用员工 | EmployeeAppService.findAvailable() | 1.按条件查询<br>2.组装DTO | Employee | EmployeeRepository.findByQuery() |

## 2. 核心服务序列图

### 2.1 创建员工

```
用户界面
    │
    │ 1. createEmployee(request)
    ▼
EmployeeAppService
    │
    │ 2. validateIdCard(idCard)
    ▼
EmployeeDomainService
    │
    │ 3. existsByIdCard(idCard)
    ▼
EmployeeRepository
    │
    │ 4. return false
    ▼
EmployeeDomainService
    │
    │ 5. validatePhone(phone)
    ▼
EmployeeDomainService
    │
    │ 6. existsByPhone(phone)
    ▼
EmployeeRepository
    │
    │ 7. return false
    ▼
EmployeeAppService
    │
    │ 8. create(name, gender, ...)
    ▼
Employee (聚合根)
    │
    │ 9. new Employee(...)
    │    - 设置初始状态 PENDING
    │    - 记录创建时间
    ▼
EmployeeAppService
    │
    │ 10. save(employee)
    ▼
EmployeeRepository
    │
    │ 11. return employee
    ▼
EmployeeAppService
    │
    │ 12. return EmployeeResponse
```

### 2.2 办理调岗

```
用户界面
    │
    │ 1. transferEmployee(employeeId, newDeptId, newPosId)
    ▼
EmployeeAppService
    │
    │ 2. findById(employeeId)
    ▼
EmployeeRepository
    │
    │ 3. return employee
    ▼
EmployeeAppService
    │
    │ 4. getDepartment(newDeptId)
    ▼
OrgRemoteService (ACL)
    │
    │ 5. GET /org/departments/{id}
    ▼
Org上下文 (REST API)
    │
    │ 6. return DepartmentDTO
    ▼
EmployeeAppService
    │
    │ 7. getPosition(newPosId)
    ▼
OrgRemoteService (ACL)
    │
    │ 8. GET /org/positions/{id}
    ▼
Org上下文 (REST API)
    │
    │ 9. return PositionDTO
    ▼
EmployeeAppService
    │
    │ 10. startTransfer(newDeptId, newPosId)
    ▼
Employee (聚合根)
    │
    │ 11. 状态验证
    │    - 只有REGULAR状态可以调岗
    │    - 设置状态为 TRANSFERRING
    │    - 记录状态变更历史
    ▼
EmployeeAppService
    │
    │ 12. save(employee)
    ▼
EmployeeRepository
    │
    │ 13. return employee
    ▼
EmployeeAppService
    │
    │ 14. 发布 EmployeeTransferred 事件
    ▼
EventBus
    │
    │ 15. 通知订阅者（如Org上下文更新部门人员统计）
```

### 2.3 培训报名

```
用户界面
    │
    │ 1. enroll(planId, employeeId)
    ▼
TrainingAppService
    │
    │ 2. findById(planId)
    ▼
TrainingRepository
    │
    │ 3. return trainingPlan
    ▼
TrainingAppService
    │
    │ 4. enroll(employeeId)
    ▼
TrainingPlan (聚合根)
    │
    │ 5. 状态验证
    │    - 只有PUBLISHED状态可以报名
    │ 6. 容量验证
    │    - 当前报名数 < 容量
    │ 7. 重复验证
    │    - 检查是否已报名
    │ 8. 创建 TrainingEnrollment
    ▼
TrainingAppService
    │
    │ 9. save(trainingPlan)
    ▼
TrainingRepository
    │
    │ 10. return trainingPlan
    ▼
TrainingAppService
    │
    │ 11. 发布 TrainingEnrolled 事件
    ▼
EventBus
```

## 3. 应用服务接口设计

### 3.1 EmployeeAppService

```java
public interface EmployeeAppService {
    EmployeeResponse createEmployee(CreateEmployeeRequest request);
    void updateEmployee(String id, UpdateEmployeeRequest request);
    void assignDepartment(String employeeId, String departmentId);
    void resign(String employeeId, String reason);
    void transfer(String employeeId, String newDepartmentId, String newPositionId);
    void addSkill(String employeeId, String skillName, SkillLevel level);
    void removeSkill(String employeeId, String skillName);
    EmployeeResponse getEmployee(String id);
    List<AvailableEmployeeResponse> findAvailableEmployees(AvailableEmployeeQuery query);
}
```

### 3.2 TalentAppService

```java
public interface TalentAppService {
    TalentResponse createTalent(CreateTalentRequest request);
    void updateStatus(String talentId, TalentStatus newStatus);
    EmployeeResponse convertToEmployee(String talentId, CreateEmployeeRequest request);
    TalentResponse getTalent(String id);
    List<TalentResponse> queryTalents(TalentQuery query);
}
```

### 3.3 RecruitmentAppService

```java
public interface RecruitmentAppService {
    RecruitmentResponse submitRequirement(CreateRecruitmentRequest request);
    void approveRequirement(String requirementId);
    void rejectRequirement(String requirementId, String reason);
    InterviewResponse scheduleInterview(String requirementId, ScheduleInterviewRequest request);
    void completeInterview(String interviewId, InterviewResult result, String feedback);
    RecruitmentResponse getRequirement(String id);
}
```

### 3.4 AttendanceAppService

```java
public interface AttendanceAppService {
    void checkIn(String employeeId, LocalDateTime checkInTime);
    void checkOut(String employeeId, LocalDateTime checkOutTime);
    AttendanceResponse getAttendance(String employeeId, LocalDate date);
    List<AttendanceResponse> getMonthlyAttendance(String employeeId, YearMonth month);
}
```

### 3.5 TrainingAppService

```java
public interface TrainingAppService {
    TrainingResponse createPlan(CreateTrainingPlanRequest request);
    void publishPlan(String planId);
    EnrollmentResponse enroll(String planId, String employeeId);
    void cancelPlan(String planId);
    TrainingResponse getPlan(String id);
    List<TrainingResponse> listPlans(TrainingPlanQuery query);
}
```

## 4. 请求/响应DTO设计

### 4.1 CreateEmployeeRequest

```java
public class CreateEmployeeRequest {
    private String name;
    private Gender gender;
    private String idCard;
    private String phone;
    private String email;
    private String departmentId;
    private String positionId;
}
```

### 4.2 EmployeeResponse

```java
public class EmployeeResponse {
    private String id;
    private String name;
    private Gender gender;
    private String phone;
    private String email;
    private EmployeeStatus status;
    private LocalDate hireDate;
    private DepartmentResponse department;
    private PositionResponse position;
    private List<SkillResponse> skills;
}
```

### 4.3 AvailableEmployeeQuery

```java
public class AvailableEmployeeQuery {
    private String departmentId;
    private String positionId;
    private Set<SkillLevel> requiredSkills;
    private LocalDate availableDate;
}
```

## 5. 事务边界

### 5.1 单聚合事务

大多数应用服务操作单个聚合，使用`@Transactional`保证原子性：

```java
@Transactional
public EmployeeResponse createEmployee(CreateEmployeeRequest request) {
    // 操作单个Employee聚合
    Employee employee = employeeDomainService.createEmployee(request);
    employeeRepository.save(employee);
    return toResponse(employee);
}
```

### 5.2 跨聚合事务（最终一致性）

跨聚合操作不使用分布式事务，而是通过**领域事件**实现最终一致性：

```java
@Transactional
public void convertToEmployee(String talentId, CreateEmployeeRequest request) {
    // 1. Talent聚合内操作
    Talent talent = talentRepository.findById(talentId);
    talent.convertToEmployee();

    // 2. 创建Employee聚合
    Employee employee = Employee.create(...);
    employeeRepository.save(employee);

    // 3. Talent转为员工后不需要其他操作
    // 领域事件会触发后续流程（如通知、统计更新等）
}
```

### 5.3 跨上下文事务

跨上下文调用通过**ACL（防腐层）**访问远程服务，不保证事务一致性：

```java
public void transfer(String employeeId, String newDeptId) {
    // 1. HR上下文内操作（事务性）
    Employee employee = employeeRepository.findById(employeeId);
    employee.startTransfer(newDeptId);
    employeeRepository.save(employee);

    // 2. 调用Org上下文（非事务性，最终一致性）
    // Org上下文通过监听EmployeeTransferred事件更新统计
}
```

## 6. 权限控制

权限控制在应用服务层通过AOP实现：

```java
@RequiresPermission("employee:create")
public EmployeeResponse createEmployee(CreateEmployeeRequest request) {
    // ...
}

@RequiresPermission("employee:transfer")
public void transfer(String employeeId, String newDeptId) {
    // 只有HR部门员工可以操作
}
```
