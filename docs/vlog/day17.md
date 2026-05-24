# Day 17: 服务驱动设计

**日期**: 2026-06-02
**阶段**: Phase 3a - HR限界上下文建模
**参与者**: 张毅、周涛、王强

## Vlog：任务树分解

今天做服务驱动设计。周涛说DDD的核心思想是**将业务服务分解为任务，分配给领域对象**。

### 任务树方法

周涛介绍了任务树分解方法：

```
业务服务（用户视角）
  └── 任务（应用层协调）
      └── 领域行为（领域对象执行）
```

### 示例：创建员工业务服务

**业务服务**: 创建员工

**任务分解**:
1. 验证身份证号唯一性（领域服务）
2. 验证手机号唯一性（领域服务）
3. 创建Employee聚合（聚合根）
4. 保存到资源库（基础设施）
5. 发布领域事件（基础设施）

**序列图**:
```
用户 → EmployeeAppService.createEmployee()
     → EmployeeDomainService.validateIdCard()
     → EmployeeDomainService.validatePhone()
     → Employee.create()
     → EmployeeRepository.save()
     → (发布 EmployeeCreated 事件)
```

### 示例：办理调岗业务服务

**业务服务**: 办理调岗

**任务分解**:
1. 验证员工存在（资源库）
2. 验证目标部门/岗位存在（Org上下文远程服务）
3. 调用Employee.startTransfer()（聚合根）
4. 更新Employee（资源库）
5. 发布领域事件（基础设施）

**序列图**:
```
用户 → EmployeeAppService.transferEmployee()
     → EmployeeRepository.findById()
     → OrgRemoteService.getDepartment()
     → OrgRemoteService.getPosition()
     → Employee.startTransfer()
     → EmployeeRepository.save()
     → (发布 EmployeeTransferred 事件)
```

### 与王强的讨论

王强问：为什么不用一个Service层做所有事情？

周涛说：这是**贫血模型 vs 充血模型**的区别。

**贫血模型**（反模式）:
```java
// Service层包含所有业务逻辑
class EmployeeService {
    void transfer(String id, String newDept) {
        // 所有逻辑都在这里
        employee.setStatus("TRANSFERRING");
        employee.setDepartment(newDept);
        repository.save(employee);
    }
}

// Employee是纯数据载体
class Employee {
    void setStatus(String status) { ... }
    void setDepartment(String dept) { ... }
}
```

**充血模型**（DDD推荐）:
```java
// AppService只是协调
class EmployeeAppService {
    void transfer(String id, String newDept) {
        Employee employee = repository.findById(id);
        employee.transfer(newDept);  // 业务逻辑在聚合根内
        repository.save(employee);
    }
}

// Employee包含业务逻辑
class Employee {
    void transfer(String newDept) {
        if (status != REGULAR) throw ...;
        this.status = TRANSFERRING;
        this.department = newDept;
    }
}
```

王强点头：充血模型的好处是业务逻辑集中，容易被复用和测试。

### 12个业务服务的任务分解

周涛把12个HR业务服务都分解了：

1. **创建员工** → EmployeeDomainService验证 → Employee.create()
2. **分配组织** → Employee.transfer()
3. **办理离职** → Employee.resign()
4. **办理调岗** → Employee.transfer()
5. **提交招聘需求** → RecruitmentRequirement.submit()
6. **安排面试** → RecruitmentRequirement.scheduleInterview()
7. **记录考勤** → AttendanceRecord.checkIn() / checkOut()
8. **创建培训计划** → TrainingPlan.create()
9. **培训报名** → TrainingPlan.enroll()
10. **维护技能档案** → Employee.addSkill() / removeSkill()
11. **管理储备人才** → Talent.create() / updateStatus() / convertToEmployee()
12. **查询可用员工** → EmployeeRepository查询（只读服务）

### 应用服务层职责

周涛总结了应用服务的职责：
1. **协调** - 调用多个领域对象完成业务
2. **事务控制** - 确保操作原子性
3. **跨上下文调用** - 与其他上下文交互
4. **权限检查** - 验证用户权限（可以放在AOP）

应用服务**不包含**业务逻辑——业务逻辑必须在领域层。

### 明天开始编码

明天开始TDD编码——先写测试，再写实现。预计1天完成所有聚合根和单元测试。

---

*记录人：张毅*
