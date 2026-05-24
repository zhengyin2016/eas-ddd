# Day 15: HR领域快速建模

**日期**: 2026-05-31
**阶段**: Phase 3a - HR限界上下文建模
**参与者**: 张毅、周涛、王强

## Vlog：快速建模法实践

今天和周涛一起做HR领域的快速建模。我们用的是"事件风暴"简化版——从业务服务出发，快速识别领域对象和领域行为。

### 第一步：列出HR业务服务

周一讨论的12个HR业务服务：
1. 创建员工
2. 分配组织
3. 办理离职
4. 办理调岗
5. 提交招聘需求
6. 安排面试
7. 记录考勤
8. 创建培训计划
9. 培训报名
10. 维护技能档案
11. 管理储备人才
12. 查询可用员工

### 第二步：识别领域对象

周涛用了一个很实用的方法——**从动词找名词**：

- "创建员工" → Employee（员工）
- "分配组织" → Department（部门）、Position（岗位）
- "办理离职" → EmployeeStatus（员工状态）
- "提交招聘需求" → RecruitmentRequirement（招聘需求）
- "安排面试" → Interview（面试）
- "记录考勤" → AttendanceRecord（考勤记录）
- "创建培训计划" → TrainingPlan（培训计划）
- "培训报名" → TrainingEnrollment（培训报名）
- "维护技能档案" → Skill（技能）
- "管理储备人才" → Talent（储备人才）

### 第三步：识别领域行为

周涛把这些行为分配给领域对象：

**Employee的行为：**
- create() - 创建
- resign() - 离职
- transfer() - 调岗
- updateInfo() - 更新信息
- addSkill() - 添加技能
- removeSkill() - 移除技能

**Talent的行为：**
- create() - 创建
- updateStatus() - 更新状态
- convertToEmployee() - 转为员工

**RecruitmentRequirement的行为：**
- submit() - 提交
- approve() - 审批通过
- reject() - 审批拒绝

**AttendanceRecord的行为：**
- checkIn() - 签到
- checkOut() - 签退

**TrainingPlan的行为：**
- create() - 创建
- publish() - 发布
- enroll() - 报名

### 关键发现：Employee的状态复杂性

王强指出Employee的状态很复杂。我们画了一个状态机：

```
待入职 → 试用期 → 正式
               ↓
            调岗中 → 正式（新岗位）
               ↓
            离职中 → 已离职
```

周涛说这个状态机是Employee聚合的核心不变量——状态转换必须遵守这个规则，不能从"调岗中"直接跳到"已离职"，必须先回到"正式"。

### 聚合划分讨论

周涛提出5个聚合：
1. **Employee聚合** - 员工及其技能、状态
2. **Talent聚合** - 储备人才
3. **Recruitment聚合** - 招聘需求及面试安排
4. **Attendance聚合** - 考勤记录
5. **Training聚合** - 培训计划及报名

王强问：为什么不把Skill做成独立聚合？

周涛解释：技能是员工的一部分，没有独立生命周期。一个技能不能脱离员工存在，所以它是Employee聚合内的值对象，不是独立聚合。

这是DDD中"聚合设计"的核心原则——**聚合是数据一致性边界，不是业务模块划分**。

### 明天继续

明天周涛要详细设计每个聚合的内部结构，用"庖丁解牛"的方式。

---

*记录人：张毅*
