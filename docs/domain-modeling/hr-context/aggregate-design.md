# HR聚合设计文档

## 1. Employee聚合

### 1.1 聚合结构

```
Employee (聚合根)
├── EmployeeId (值对象)
├── EmployeeStatus (值对象)
├── Gender (值对象)
├── DepartmentId (值对象) - 引用Org上下文
├── PositionId (值对象) - 引用Org上下文
├── List<Skill> (值对象列表)
└── List<StatusChange> (值对象列表)
```

### 1.2 聚合根设计

**类名**: `Employee`

**属性**:
| 属性名 | 类型 | 说明 |
|-------|------|------|
| id | EmployeeId | 员工ID |
| name | String | 姓名 |
| gender | Gender | 性别 |
| idCard | String | 身份证号 |
| phone | String | 手机号 |
| email | String | 邮箱 |
| status | EmployeeStatus | 员工状态 |
| hireDate | LocalDate | 入职日期 |
| departmentId | DepartmentId | 部门ID |
| positionId | PositionId | 岗位ID |
| skills | List\<Skill\> | 技能列表 |
| statusHistory | List\<StatusChange\> | 状态变更历史 |

**行为**:
- `create(name, gender, idCard, phone, email, departmentId, positionId)` - 创建员工
- `resign(reason)` - 办理离职
- `transfer(newDepartmentId, newPositionId)` - 办理调岗
- `updateInfo(name, phone, email)` - 更新基本信息
- `addSkill(skill)` - 添加技能
- `removeSkill(skillName)` - 移除技能
- `confirmProbation()` - 试用期转正
- `startTransfer()` - 开始调岗流程

### 1.3 值对象设计

**EmployeeStatus** - 枚举
```java
public enum EmployeeStatus {
    PENDING,      // 待入职
    PROBATION,    // 试用期
    REGULAR,      // 正式
    TRANSFERRING, // 调岗中
    RESIGNING,    // 离职中
    RESIGNED      // 已离职
}
```

**Gender** - 枚举
```java
public enum Gender {
    MALE,   // 男
    FEMALE  // 女
}
```

**Skill** - Record
```java
public record Skill(
    String name,
    SkillLevel level,
    LocalDate certifiedDate
) {}
```

### 1.4 不变量

1. **状态机约束**: 状态转换必须遵守以下规则
   - PENDING → PROBATION（入职）
   - PROBATION → REGULAR（转正）
   - REGULAR → TRANSFERRING → REGULAR（调岗）
   - REGULAR → RESIGNING → RESIGNED（离职）

2. **唯一性约束**: 身份证号、手机号在系统中必须唯一

3. **业务规则**: 只有正式员工才能调岗

### 1.5 领域事件

- `EmployeeCreated` - 员工创建
- `EmployeeResigned` - 员工离职
- `EmployeeTransferred` - 员工调岗
- `EmployeeProbationConfirmed` - 试用期转正

## 2. Talent聚合

### 2.1 聚合结构

```
Talent (聚合根)
├── TalentId (值对象)
├── TalentStatus (值对象)
├── ContactInfo (值对象)
└── List<Skill> (值对象列表)
```

### 2.2 聚合根设计

**类名**: `Talent`

**属性**:
| 属性名 | 类型 | 说明 |
|-------|------|------|
| id | TalentId | 储备人才ID |
| name | String | 姓名 |
| source | TalentSource | 来源 |
| contactInfo | ContactInfo | 联系信息 |
| skills | List\<Skill\> | 技能列表 |
| status | TalentStatus | 状态 |

**行为**:
- `create(name, source, contactInfo, skills)` - 创建储备人才
- `updateStatus(newStatus)` - 更新状态
- `convertToEmployee(employeeId)` - 转为员工

### 2.3 不变量

1. **状态单向流转**: NEW → CONTACTING → INTERVIEWED → APPROVED/REJECTED
2. **转换锁定**: 转为员工后状态锁定，不能再次变更

## 3. Recruitment聚合

### 3.1 聚合结构

```
RecruitmentRequirement (聚合根)
├── RecruitmentId (值对象)
├── RecruitmentStatus (值对象)
└── List<Interview> (实体列表)
    ├── InterviewId (值对象)
    ├── InterviewResult (值对象)
    └── ...
```

### 3.2 聚合根设计

**类名**: `RecruitmentRequirement`

**属性**:
| 属性名 | 类型 | 说明 |
|-------|------|------|
| id | RecruitmentId | 招聘需求ID |
| departmentId | DepartmentId | 部门ID |
| positionId | PositionId | 岗位ID |
| count | int | 招聘人数 |
| description | String | 岗位描述 |
| status | RecruitmentStatus | 状态 |
| createdAt | LocalDateTime | 创建时间 |
| interviews | List\<Interview\> | 面试安排 |

**行为**:
- `submit(departmentId, positionId, count, description)` - 提交需求
- `approve()` - 审批通过
- `reject(reason)` - 审批拒绝
- `cancel()` - 取消需求
- `scheduleInterview(candidateName, time, interviewer)` - 安排面试

### 3.3 实体设计

**Interview** - 实体
| 属性名 | 类型 | 说明 |
|-------|------|------|
| id | InterviewId | 面试ID |
| candidateName | String | 候选人姓名 |
| time | LocalDateTime | 面试时间 |
| interviewer | String | 面试官 |
| result | InterviewResult | 面试结果 |
| feedback | String | 面试反馈 |

### 3.4 不变量

1. **状态约束**: 只有PENDING状态的招聘需求才能审批
2. **面试数量**: 面试人数不能超过招聘人数
3. **完成约束**: 已完成(FULFILLED)的招聘需求不能修改

## 4. Attendance聚合

### 4.1 聚合结构

```
AttendanceRecord (聚合根)
├── AttendanceId (值对象)
└── AttendanceStatus (值对象)
```

### 4.2 聚合根设计

**类名**: `AttendanceRecord`

**属性**:
| 属性名 | 类型 | 说明 |
|-------|------|------|
| id | AttendanceId | 考勤ID |
| employeeId | EmployeeId | 员工ID |
| date | LocalDate | 考勤日期 |
| checkInTime | LocalDateTime | 签到时间 |
| checkOutTime | LocalDateTime | 签退时间 |
| status | AttendanceStatus | 考勤状态 |
| workHours | BigDecimal | 工作时长 |

**行为**:
- `checkIn(time)` - 签到
- `checkOut(time)` - 签退
- `calculateWorkHours()` - 计算工作时长

### 4.3 不变量

1. **唯一性**: 每个员工每天只能有一条考勤记录
2. **时间约束**: 签退时间必须晚于签到时间
3. **状态规则**: 
    - 9:00后签到 = LATE
    - 18:00前签退 = EARLY_LEAVE
    - 无签到记录 = ABSENT

## 5. Training聚合

### 5.1 聚合结构

```
TrainingPlan (聚合根)
├── TrainingId (值对象)
├── TrainingStatus (值对象)
└── List<TrainingEnrollment> (实体列表)
    ├── EnrollmentId (值对象)
    └── EnrollmentStatus (值对象)
```

### 5.2 聚合根设计

**类名**: `TrainingPlan`

**属性**:
| 属性名 | 类型 | 说明 |
|-------|------|------|
| id | TrainingId | 培训计划ID |
| name | String | 培训名称 |
| description | String | 培训描述 |
| startDate | LocalDate | 开始日期 |
| endDate | LocalDate | 结束日期 |
| capacity | int | 容量限制 |
| status | TrainingStatus | 状态 |
| enrollments | List\<TrainingEnrollment\> | 报名列表 |

**行为**:
- `create(name, description, startDate, endDate, capacity)` - 创建培训
- `publish()` - 发布培训
- `enroll(employeeId)` - 报名
- `cancel()` - 取消培训
- `complete()` - 完成培训

### 5.3 实体设计

**TrainingEnrollment** - 实体
| 属性名 | 类型 | 说明 |
|-------|------|------|
| id | EnrollmentId | 报名ID |
| planId | TrainingId | 培训计划ID |
| employeeId | EmployeeId | 员工ID |
| status | EnrollmentStatus | 报名状态 |
| checkInTime | LocalDateTime | 签到时间 |
| completionRate | BigDecimal | 完成进度 |

### 5.4 不变量

1. **容量约束**: 报名人数不能超过容量限制
2. **状态约束**: 只有PUBLISHED状态的培训才能报名
3. **重复约束**: 同一员工不能重复报名同一培训

## 6. 聚合间关系

```
┌─────────────┐         ┌─────────────┐
│  Employee   │         │    Talent   │
│  (聚合根)    │         │   (聚合根)   │
└─────────────┘         └─────────────┘
       ▲                       │
       │                       │ convertToEmployee()
       │                       ▼
       │                 ┌─────────────┐
       │                 │  Employee   │
       │                 │  (聚合根)    │
       │                 └─────────────┘
       │
       │ employeeId
       │
       ▼
┌─────────────┐         ┌─────────────┐
│ Attendance  │         │  Training   │
│  (聚合根)    │         │  (聚合根)    │
└─────────────┘         └─────────────┘
                              ▲
                              │
                              │ enrollments.employeeId
                              │
                              ▼
                        ┌─────────────┐
                        │  Employee   │
                        │  (聚合根)    │
                        └─────────────┘
```

**重要**: 所有聚合间关系通过ID引用，不直接持有对象引用。
