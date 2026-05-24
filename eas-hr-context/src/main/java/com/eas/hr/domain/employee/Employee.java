package com.eas.hr.domain.employee;

import com.eas.common.ddd.AggregateRoot;
import com.eas.hr.domain.event.EmployeeCreatedEvent;
import com.eas.hr.domain.event.EmployeeResignedEvent;
import com.eas.hr.domain.event.EmployeeTransferredEvent;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 员工聚合根
 * <p>
 * 员工是HR上下文的核心聚合，包含员工的基本信息、技能、状态等。
 * 聚合负责维护员工状态转换的业务规则。
 * </p>
 */
public class Employee extends AggregateRoot<EmployeeId> {

    private String name;
    private Gender gender;
    private String idCard;
    private String phone;
    private String email;
    private EmployeeStatus status;
    private LocalDate hireDate;
    private DepartmentId departmentId;
    private PositionId positionId;
    private List<Skill> skills;
    private List<StatusChange> statusHistory;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * 私有构造函数，通过工厂方法创建
     */
    private Employee(EmployeeId id, String name, Gender gender, String idCard, String phone, String email,
                     DepartmentId departmentId, PositionId positionId) {
        super(id);
        this.name = Objects.requireNonNull(name, "Name cannot be null");
        this.gender = Objects.requireNonNull(gender, "Gender cannot be null");
        this.idCard = Objects.requireNonNull(idCard, "ID card cannot be null");
        this.phone = Objects.requireNonNull(phone, "Phone cannot be null");
        this.email = Objects.requireNonNull(email, "Email cannot be null");
        this.departmentId = Objects.requireNonNull(departmentId, "Department ID cannot be null");
        this.positionId = Objects.requireNonNull(positionId, "Position ID cannot be null");
        this.status = EmployeeStatus.PENDING;
        this.hireDate = LocalDate.now();
        this.skills = new ArrayList<>();
        this.statusHistory = new ArrayList<>();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();

        // 记录初始状态
        this.statusHistory.add(StatusChange.of(null, EmployeeStatus.PENDING));
    }

    /**
     * 创建新员工
     *
     * @param name         姓名
     * @param gender       性别
     * @param idCard       身份证号
     * @param phone        手机号
     * @param email        邮箱
     * @param departmentId 部门ID
     * @param positionId   岗位ID
     * @return 新创建的员工
     */
    public static Employee create(String name, Gender gender, String idCard, String phone, String email,
                                  DepartmentId departmentId, PositionId positionId) {
        Employee employee = new Employee(EmployeeId.generate(), name, gender, idCard, phone, email,
                departmentId, positionId);
        employee.addDomainEvent(new EmployeeCreatedEvent(employee.getId().value(), employee.name));
        return employee;
    }

    /**
     * 从持久化重建员工对象
     *
     * @param id            员工ID
     * @param name          姓名
     * @param gender        性别
     * @param idCard        身份证号
     * @param phone         手机号
     * @param email         邮箱
     * @param status        状态
     * @param hireDate      入职日期
     * @param departmentId  部门ID
     * @param positionId    岗位ID
     * @param skills        技能列表
     * @param statusHistory 状态历史
     * @param createdAt     创建时间
     * @param updatedAt     更新时间
     * @return 员工对象
     */
    public static Employee restore(EmployeeId id, String name, Gender gender, String idCard, String phone, String email,
                                   EmployeeStatus status, LocalDate hireDate, DepartmentId departmentId, PositionId positionId,
                                   List<Skill> skills, List<StatusChange> statusHistory,
                                   LocalDateTime createdAt, LocalDateTime updatedAt) {
        Employee employee = new Employee(id, name, gender, idCard, phone, email, departmentId, positionId);
        employee.status = status;
        employee.hireDate = hireDate;
        employee.skills = new ArrayList<>(skills);
        employee.statusHistory = new ArrayList<>(statusHistory);
        employee.createdAt = createdAt;
        employee.updatedAt = updatedAt;
        return employee;
    }

    /**
     * 办理入职（从待入职转为试用期）
     */
    public void onboard() {
        if (status != EmployeeStatus.PENDING) {
            throw new IllegalStateException("Only PENDING status can be onboarded");
        }
        changeStatus(EmployeeStatus.PROBATION, "入职");
    }

    /**
     * 试用期转正
     */
    public void confirmProbation() {
        if (status != EmployeeStatus.PROBATION) {
            throw new IllegalStateException("Only PROBATION status can be confirmed");
        }
        changeStatus(EmployeeStatus.REGULAR, "转正");
    }

    /**
     * 开始调岗流程
     *
     * @param newDepartmentId 新部门ID
     * @param newPositionId   新岗位ID
     */
    public void startTransfer(DepartmentId newDepartmentId, PositionId newPositionId) {
        if (status != EmployeeStatus.REGULAR) {
            throw new IllegalStateException("Only REGULAR employees can transfer");
        }

        DepartmentId oldDepartment = this.departmentId;
        PositionId oldPosition = this.positionId;

        changeStatus(EmployeeStatus.TRANSFERRING, "开始调岗");
        this.departmentId = Objects.requireNonNull(newDepartmentId, "New department ID cannot be null");
        this.positionId = Objects.requireNonNull(newPositionId, "New position ID cannot be null");

        addDomainEvent(new EmployeeTransferredEvent(
                getId().value(),
                oldDepartment.value(),
                oldPosition.value(),
                newDepartmentId.value(),
                newPositionId.value()
        ));
    }

    /**
     * 完成调岗
     */
    public void completeTransfer() {
        if (status != EmployeeStatus.TRANSFERRING) {
            throw new IllegalStateException("Only TRANSFERRING status can complete transfer");
        }
        changeStatus(EmployeeStatus.REGULAR, "调岗完成");
    }

    /**
     * 办理离职
     *
     * @param reason 离职原因
     */
    public void resign(String reason) {
        if (status == EmployeeStatus.RESIGNED) {
            throw new IllegalStateException("Employee is already resigned");
        }
        if (status == EmployeeStatus.RESIGNING) {
            throw new IllegalStateException("Employee is already resigning");
        }

        EmployeeStatus oldStatus = this.status;
        changeStatus(EmployeeStatus.RESIGNING, reason);
        addDomainEvent(new EmployeeResignedEvent(getId().value(), name, oldStatus.name(), reason));
    }

    /**
     * 确认离职完成
     */
    public void confirmResignation() {
        if (status != EmployeeStatus.RESIGNING) {
            throw new IllegalStateException("Only RESIGNING status can confirm resignation");
        }
        changeStatus(EmployeeStatus.RESIGNED, "离职完成");
    }

    /**
     * 更新基本信息
     *
     * @param name  姓名
     * @param phone 手机号
     * @param email 邮箱
     */
    public void updateInfo(String name, String phone, String email) {
        if (status == EmployeeStatus.RESIGNED) {
            throw new IllegalStateException("Cannot update resigned employee");
        }
        this.name = Objects.requireNonNull(name, "Name cannot be null");
        this.phone = Objects.requireNonNull(phone, "Phone cannot be null");
        this.email = Objects.requireNonNull(email, "Email cannot be null");
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 分配到新部门
     *
     * @param newDepartmentId 新部门ID
     */
    public void assignDepartment(DepartmentId newDepartmentId) {
        if (status == EmployeeStatus.RESIGNED) {
            throw new IllegalStateException("Cannot assign department to resigned employee");
        }
        this.departmentId = Objects.requireNonNull(newDepartmentId, "Department ID cannot be null");
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 添加技能
     *
     * @param skill 技能
     */
    public void addSkill(Skill skill) {
        Objects.requireNonNull(skill, "Skill cannot be null");

        // 检查是否已存在同名技能
        skills.removeIf(s -> s.name().equals(skill.name()));
        skills.add(skill);
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 移除技能
     *
     * @param skillName 技能名称
     */
    public void removeSkill(String skillName) {
        boolean removed = skills.removeIf(skill -> skill.name().equals(skillName));
        if (removed) {
            this.updatedAt = LocalDateTime.now();
        }
    }

    /**
     * 更改状态（私有方法，记录状态变更历史）
     *
     * @param newStatus 新状态
     * @param reason    原因
     */
    private void changeStatus(EmployeeStatus newStatus, String reason) {
        EmployeeStatus oldStatus = this.status;
        this.status = newStatus;
        this.statusHistory.add(StatusChange.withReason(oldStatus, newStatus, reason));
        this.updatedAt = LocalDateTime.now();
    }

    // Getters

    public String getName() {
        return name;
    }

    public Gender getGender() {
        return gender;
    }

    public String getIdCard() {
        return idCard;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    public EmployeeStatus getStatus() {
        return status;
    }

    public LocalDate getHireDate() {
        return hireDate;
    }

    public DepartmentId getDepartmentId() {
        return departmentId;
    }

    public PositionId getPositionId() {
        return positionId;
    }

    public List<Skill> getSkills() {
        return new ArrayList<>(skills);
    }

    public List<StatusChange> getStatusHistory() {
        return new ArrayList<>(statusHistory);
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    /**
     * 检查是否为正式员工
     *
     * @return true如果是正式员工
     */
    public boolean isRegular() {
        return status == EmployeeStatus.REGULAR;
    }

    /**
     * 检查是否可以调岗
     *
     * @return true如果可以调岗
     */
    public boolean canTransfer() {
        return status == EmployeeStatus.REGULAR;
    }

    /**
     * 检查是否已离职
     *
     * @return true如果已离职
     */
    public boolean isResigned() {
        return status == EmployeeStatus.RESIGNED;
    }
}
