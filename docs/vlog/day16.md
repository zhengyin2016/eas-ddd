# Day 16: 聚合设计 - 庖丁解牛

**日期**: 2026-06-01
**阶段**: Phase 3a - HR限界上下文建模
**参与者**: 张毅、周涛、王强

## Vlog：聚合设计

今天周涛用"庖丁解牛"的方式设计聚合。他说聚合设计是DDD的核心——**聚合是数据一致性边界，也是事务边界**。

### Employee聚合设计

周涛画了一张图：

```
┌─────────────────────────────────────────────┐
│              Employee 聚合                   │
├─────────────────────────────────────────────┤
│  ┌───────────────────────────────────────┐  │
│  │  Employee (聚合根)                     │  │
│  ├───────────────────────────────────────┤  │
│  │  - id: EmployeeId                      │  │
│  │  - name: String                        │  │
│  │  - gender: Gender                      │  │
│  │  - idCard: String                      │  │
│  │  - phone: String                       │  │
│  │  - email: String                       │  │
│  │  - status: EmployeeStatus              │  │
│  │  - hireDate: LocalDate                 │  │
│  │  - departmentId: DepartmentId          │  │
│  │  - positionId: PositionId              │  │
│  │  - skills: List<Skill>                 │  │
│  │  - statusHistory: List<StatusChange>   │  │
│  └───────────────────────────────────────┘  │
│                                             │
│  ┌───────────────────────────────────────┐  │
│  │  Skill (值对象)                        │  │
│  ├───────────────────────────────────────┤  │
│  │  - name: String                        │  │
│  │  - level: SkillLevel                   │  │
│  │  - certifiedDate: LocalDate?           │  │
│  └───────────────────────────────────────┘  │
│                                             │
│  ┌───────────────────────────────────────┐  │
│  │  StatusChange (值对象)                  │  │
│  ├───────────────────────────────────────┤  │
│  │  - fromStatus: EmployeeStatus          │  │
│  │  - toStatus: EmployeeStatus            │  │
│  │  - changeDate: LocalDateTime           │  │
│  │  - reason: String?                     │  │
│  └───────────────────────────────────────┘  │
└─────────────────────────────────────────────┘
```

周涛解释：
- **聚合根**: Employee，所有外部访问必须通过Employee
- **值对象**: Skill、StatusChange，没有独立标识，只能被Employee持有
- **不变量**: 状态转换必须符合状态机规则

### 跨聚合边界的关键讨论

王强问：Employee应该直接引用Department对象吗？

周涛说：**不应该！这是跨聚合边界的常见错误**。

他画了两种设计：

```
错误设计：
Employee {
  Department department;  // 直接持有对象引用
}

正确设计：
Employee {
  DepartmentId departmentId;  // 只持有ID
}
```

为什么？
1. **Department属于Org上下文**，不在HR上下文内
2. **跨聚合引用会造成数据一致性问题**——如果Department被删除，Employee怎么办？
3. **违反聚合独立修改原则**——修改Department会影响Employee聚合

王强点头：如果需要显示部门名称怎么办？

周涛说：在应用服务层组装——先从EmployeeRepository获取Employee，然后用departmentId调用Org上下文的服务获取Department信息。

这是**菱形对称架构**的核心思想：领域层保持纯净，跨上下文的协调在应用层做。

### 其他聚合设计

**Talent聚合** - 比较简单，储备人才转为员工后状态锁定

**Recruitment聚合** - 包含Interview实体列表，一个招聘需求可以有多次面试

**Attendance聚合** - 最简单，就是一条考勤记录

**Training聚合** - 包含TrainingEnrollment实体列表，报名是实体不是值对象（有独立标识和状态）

### 值对象vs实体的判断标准

周涛总结了一个判断标准：
- **值对象**: 没有独立标识，通过属性值判断相等性
- **实体**: 有独立标识，标识相同就是同一个对象

Skill是值对象——两个Java、高级、同日期的技能证书是等价的。
TrainingEnrollment是实体——同一个人报名两次，产生两条报名记录（虽然内容相同但ID不同）。

### 明天继续

明天要做服务驱动设计——把12个业务服务分解成任务树，分配给领域对象。

---

*记录人：张毅*
