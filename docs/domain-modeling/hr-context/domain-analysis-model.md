# HR领域分析模型

## 1. 领域对象清单

| 领域对象 | 类型 | 所属聚合 | 属性 | 行为 |
|---------|------|---------|------|------|
| Employee | 聚合根 | employee | id, name, gender, idCard, phone, email, status, hireDate, departmentId, positionId | create(), resign(), transfer(), updateInfo(), addSkill(), removeSkill() |
| EmployeeStatus | 值对象 | employee | PENDING, PROBATION, REGULAR, TRANSFERRING, RESIGNING, RESIGNED | - |
| Gender | 值对象 | employee | MALE, FEMALE | - |
| DepartmentId | 值对象 | employee | value (String) | - |
| PositionId | 值对象 | employee | value (String) | - |
| Skill | 值对象 | employee | name, level, certifiedDate | - |
| EmployeeId | 值对象 | employee | value (String) | - |
| Talent | 聚合根 | talent | id, name, source, contactInfo, skills, status | create(), updateStatus(), convertToEmployee() |
| TalentId | 值对象 | talent | value (String) | - |
| TalentStatus | 值对象 | talent | NEW, CONTACTING, INTERVIEWED, APPROVED, CONVERTED, REJECTED | - |
| ContactInfo | 值对象 | talent | phone, email, wechat | - |
| RecruitmentRequirement | 聚合根 | recruitment | id, departmentId, positionId, count, description, status, createdAt | submit(), approve(), reject(), cancel() |
| RecruitmentId | 值对象 | recruitment | value (String) | - |
| RecruitmentStatus | 值对象 | recruitment | DRAFT, PENDING, APPROVED, REJECTED, CANCELLED, FULFILLED | - |
| Interview | 实体 | recruitment | id, requirementId, candidateName, time, interviewer, result, feedback | schedule(), complete(), cancel() |
| InterviewId | 值对象 | recruitment | value (String) | - |
| InterviewResult | 值对象 | recruitment | PASSED, FAILED, PENDING | - |
| AttendanceRecord | 聚合根 | attendance | id, employeeId, date, checkInTime, checkOutTime, status, workHours | checkIn(), checkOut(), calculateWorkHours() |
| AttendanceId | 值对象 | attendance | value (String) | - |
| AttendanceStatus | 值对象 | attendance | NORMAL, LATE, EARLY_LEAVE, ABSENT | - |
| TrainingPlan | 聚合根 | training | id, name, description, startDate, endDate, capacity, status | create(), publish(), enroll(), cancel(), complete() |
| TrainingId | 值对象 | training | value (String) | - |
| TrainingStatus | 值对象 | training | DRAFT, PUBLISHED, IN_PROGRESS, COMPLETED, CANCELLED | - |
| TrainingEnrollment | 实体 | training | id, planId, employeeId, status, checkInTime, completionRate | enroll(), checkIn(), updateProgress(), complete() |
| EnrollmentId | 值对象 | training | value (String) | - |
| EnrollmentStatus | 值对象 | training | PENDING, CONFIRMED, ATTENDED, COMPLETED, CANCELLED | - |

## 2. 领域分析模型图

```
┌─────────────────────────────────────────────────────────────────────────────────┐
│                          HR 限界上下文                                           │
├─────────────────────────────────────────────────────────────────────────────────┤
│                                                                                 │
│  ┌──────────────────┐    ┌──────────────────┐    ┌──────────────────┐         │
│  │   Employee       │    │     Talent       │    │  Recruitment     │         │
│  │   聚合根          │    │     聚合根        │    │   聚合根          │         │
│  ├──────────────────┤    ├──────────────────┤    ├──────────────────┤         │
│  │ - id             │    │ - id             │    │ - id             │         │
│  │ - name           │    │ - name           │    │ - departmentId   │         │
│  │ - status         │    │ - source         │    │ - positionId     │         │
│  │ - departmentId   │    │ - contactInfo    │    │ - count          │         │
│  │ - positionId     │    │ - status         │    │ - status         │         │
│  │ - skills[]       │    │ - skills[]       │    │ - description    │         │
│  │                  │    │                  │    │                  │         │
│  │ + create()       │    │ + create()       │    │ + submit()       │         │
│  │ + resign()       │    │ + updateStatus() │    │ + approve()      │         │
│  │ + transfer()     │    │ + convertToEmp() │    │ + reject()       │         │
│  │ + addSkill()     │    │                  │    │                  │         │
│  └──────────────────┘    └──────────────────┘    └──────────────────┘         │
│           │                       │                       │                   │
│           │                       │                       │                   │
│           ▼                       ▼                       ▼                   │
│  ┌──────────────────┐    ┌──────────────────┐    ┌──────────────────┐         │
│  │   Attendance     │    │    Training      │    │     Interview     │         │
│  │   聚合根          │    │    聚合根         │    │     实体          │         │
│  ├──────────────────┤    ├──────────────────┤    ├──────────────────┤         │
│  │ - id             │    │ - id             │    │ - id             │         │
│  │ - employeeId     │    │ - name           │    │ - requirementId  │         │
│  │ - date           │    │ - startDate      │    │ - candidateName  │         │
│  │ - checkInTime    │    │ - endDate        │    │ - time           │         │
│  │ - checkOutTime   │    │ - capacity       │    │ - interviewer    │         │
│  │ - status         │    │ - status         │    │ - result         │         │
│  │                  │    │ - enrollments[]  │    │ - feedback       │         │
│  │ + checkIn()      │    │                  │    │                  │         │
│  │ + checkOut()     │    │ + publish()      │    │ + schedule()     │         │
│  └──────────────────┘    │ + enroll()       │    │ + complete()     │         │
│                          └──────────────────┘    └──────────────────┘         │
│                                   │                                           │
│                                   ▼                                           │
│                          ┌──────────────────┐                                 │
│                          │ TrainingEnrollment│                                │
│                          │      实体         │                                │
│                          ├──────────────────┤                                 │
│                          │ - id             │                                 │
│                          │ - planId         │                                 │
│                          │ - employeeId     │                                 │
│                          │ - status         │                                 │
│                          │ - checkInTime    │                                 │
│                          │                  │                                 │
│                          │ + enroll()       │                                 │
│                          │ + checkIn()      │                                 │
│                          └──────────────────┘                                 │
│                                                                                 │
└─────────────────────────────────────────────────────────────────────────────────┘

  跨上下文引用（通过ID）:
  ┌──────────────┐         ┌──────────────┐
  │   HR 上下文   │         │  Org 上下文   │
  ├──────────────┤         ├──────────────┤
  │  Employee    │────────▶│ Department   │
  │  departmentId│  (引用)  │  (实体)       │
  │              │         │              │
  │  Employee    │────────▶│  Position    │
  │  positionId  │  (引用)  │  (实体)       │
  └──────────────┘         └──────────────┘
```

## 3. 聚合关系说明

### Employee 聚合
- **聚合根**: Employee
- **包含实体**: 无
- **包含值对象**: EmployeeId, EmployeeStatus, Gender, DepartmentId, PositionId, Skill[]
- **关键不变量**: 状态机必须遵守转换规则（待入职→试用期→正式→调岗中/离职中→已离职）

### Talent 聚合
- **聚合根**: Talent
- **包含实体**: 无
- **包含值对象**: TalentId, TalentStatus, ContactInfo, Skill[]
- **关键不变量**: 转为员工后状态不能再次变更

### Recruitment 聚合
- **聚合根**: RecruitmentRequirement
- **包含实体**: Interview[]
- **包含值对象**: RecruitmentId, RecruitmentStatus, InterviewId, InterviewResult
- **关键不变量**: 已完成的招聘需求不能修改

### Attendance 聚合
- **聚合根**: AttendanceRecord
- **包含实体**: 无
- **包含值对象**: AttendanceId, AttendanceStatus
- **关键不变量**: 每天每个员工只能有一条考勤记录

### Training 聚合
- **聚合根**: TrainingPlan
- **包含实体**: TrainingEnrollment[]
- **包含值对象**: TrainingId, TrainingStatus, EnrollmentId, EnrollmentStatus
- **关键不变量**: 报名人数不能超过容量限制

## 4. 跨上下文引用

HR上下文需要引用Org上下文的Department和Position：

1. **Department** - 通过DepartmentId值对象引用
2. **Position** - 通过PositionId值对象引用

**重要**: HR上下文不持有Department或Position的对象引用，只保存ID。如果需要获取部门/岗位详情，通过应用服务调用Org上下文的远程服务。
