package com.eas.hr.northbound.appservice;

import com.eas.hr.domain.employee.*;
import com.eas.hr.domain.employee.service.EmployeeDomainService;
import com.eas.hr.message.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 员工应用服务
 * <p>
 * 应用服务负责协调领域对象完成业务用例，不包含业务逻辑。
 * </p>
 */
@Service
public class EmployeeAppService {

    private final EmployeeRepository employeeRepository;
    private final EmployeeDomainService employeeDomainService;

    public EmployeeAppService(EmployeeRepository employeeRepository,
                             EmployeeDomainService employeeDomainService) {
        this.employeeRepository = employeeRepository;
        this.employeeDomainService = employeeDomainService;
    }

    /**
     * 创建员工
     *
     * @param request 创建请求
     * @return 员工响应
     */
    @Transactional
    public EmployeeResponse createEmployee(CreateEmployeeRequest request) {
        // 验证身份证号格式
        if (!employeeDomainService.validateIdCardFormat(request.getIdCard())) {
            throw new IllegalArgumentException("Invalid ID card format");
        }

        // 验证手机号格式
        if (!employeeDomainService.validatePhoneFormat(request.getPhone())) {
            throw new IllegalArgumentException("Invalid phone format");
        }

        // 验证邮箱格式
        if (!employeeDomainService.validateEmailFormat(request.getEmail())) {
            throw new IllegalArgumentException("Invalid email format");
        }

        // 验证身份证号唯一性
        if (!employeeDomainService.validateIdCardUnique(request.getIdCard())) {
            throw new IllegalArgumentException("ID card already exists");
        }

        // 验证手机号唯一性
        if (!employeeDomainService.validatePhoneUnique(request.getPhone())) {
            throw new IllegalArgumentException("Phone already exists");
        }

        // 创建员工
        Employee employee = Employee.create(
                request.getName(),
                request.getGender(),
                request.getIdCard(),
                request.getPhone(),
                request.getEmail(),
                DepartmentId.of(request.getDepartmentId()),
                PositionId.of(request.getPositionId())
        );

        employee = employeeRepository.save(employee);

        return toResponse(employee);
    }

    /**
     * 更新员工基本信息
     *
     * @param id      员工ID
     * @param request 更新请求
     * @return 员工响应
     */
    @Transactional
    public EmployeeResponse updateEmployee(String id, UpdateEmployeeRequest request) {
        Employee employee = getEmployeeEntity(id);
        employee.updateInfo(request.getName(), request.getPhone(), request.getEmail());
        employee = employeeRepository.save(employee);
        return toResponse(employee);
    }

    /**
     * 分配部门
     *
     * @param employeeId   员工ID
     * @param departmentId 部门ID
     */
    @Transactional
    public void assignDepartment(String employeeId, String departmentId) {
        Employee employee = getEmployeeEntity(employeeId);
        employee.assignDepartment(DepartmentId.of(departmentId));
        employeeRepository.save(employee);
    }

    /**
     * 办理入职
     *
     * @param employeeId 员工ID
     */
    @Transactional
    public void onboard(String employeeId) {
        Employee employee = getEmployeeEntity(employeeId);
        employee.onboard();
        employeeRepository.save(employee);
    }

    /**
     * 试用期转正
     *
     * @param employeeId 员工ID
     */
    @Transactional
    public void confirmProbation(String employeeId) {
        Employee employee = getEmployeeEntity(employeeId);
        employee.confirmProbation();
        employeeRepository.save(employee);
    }

    /**
     * 办理调岗
     *
     * @param employeeId       员工ID
     * @param newDepartmentId 新部门ID
     * @param newPositionId    新岗位ID
     */
    @Transactional
    public void transferEmployee(String employeeId, String newDepartmentId, String newPositionId) {
        Employee employee = getEmployeeEntity(employeeId);
        employee.startTransfer(DepartmentId.of(newDepartmentId), PositionId.of(newPositionId));
        employeeRepository.save(employee);
    }

    /**
     * 完成调岗
     *
     * @param employeeId 员工ID
     */
    @Transactional
    public void completeTransfer(String employeeId) {
        Employee employee = getEmployeeEntity(employeeId);
        employee.completeTransfer();
        employeeRepository.save(employee);
    }

    /**
     * 办理离职
     *
     * @param employeeId 员工ID
     * @param reason     离职原因
     */
    @Transactional
    public void resign(String employeeId, String reason) {
        Employee employee = getEmployeeEntity(employeeId);
        employee.resign(reason);
        employeeRepository.save(employee);
    }

    /**
     * 确认离职完成
     *
     * @param employeeId 员工ID
     */
    @Transactional
    public void confirmResignation(String employeeId) {
        Employee employee = getEmployeeEntity(employeeId);
        employee.confirmResignation();
        employeeRepository.save(employee);
    }

    /**
     * 添加技能
     *
     * @param employeeId 员工ID
     * @param skillName  技能名称
     * @param level      技能等级
     */
    @Transactional
    public void addSkill(String employeeId, String skillName, SkillLevel level) {
        Employee employee = getEmployeeEntity(employeeId);
        employee.addSkill(Skill.of(skillName, level));
        employeeRepository.save(employee);
    }

    /**
     * 添加认证技能
     *
     * @param employeeId     员工ID
     * @param skillName      技能名称
     * @param level          技能等级
     * @param certifiedDate 认证日期
     */
    @Transactional
    public void addCertifiedSkill(String employeeId, String skillName, SkillLevel level,
                                  java.time.LocalDate certifiedDate) {
        Employee employee = getEmployeeEntity(employeeId);
        employee.addSkill(Skill.certified(skillName, level, certifiedDate));
        employeeRepository.save(employee);
    }

    /**
     * 移除技能
     *
     * @param employeeId 员工ID
     * @param skillName  技能名称
     */
    @Transactional
    public void removeSkill(String employeeId, String skillName) {
        Employee employee = getEmployeeEntity(employeeId);
        employee.removeSkill(skillName);
        employeeRepository.save(employee);
    }

    /**
     * 获取员工信息
     *
     * @param id 员工ID
     * @return 员工响应
     */
    public EmployeeResponse getEmployee(String id) {
        Employee employee = getEmployeeEntity(id);
        return toResponse(employee);
    }

    /**
     * 查询可用员工
     *
     * @param query 查询条件
     * @return 可用员工列表
     */
    public List<AvailableEmployeeResponse> findAvailableEmployees(AvailableEmployeeQuery query) {
        List<Employee> employees = employeeRepository.findAvailableEmployees(
                query.getDepartmentId(),
                query.getPositionId(),
                query.getSkillNames() != null && !query.getSkillNames().isEmpty()
                        ? query.getSkillNames().iterator().next()
                        : null
        );

        return employees.stream()
                .filter(e -> e.canTransfer())
                .map(this::toAvailableResponse)
                .collect(Collectors.toList());
    }

    /**
     * 根据部门ID查询员工
     *
     * @param departmentId 部门ID
     * @return 员工列表
     */
    public List<EmployeeResponse> findByDepartment(String departmentId) {
        List<Employee> employees = employeeRepository.findByDepartmentId(departmentId);
        return employees.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // Helper methods

    private Employee getEmployeeEntity(String id) {
        EmployeeId employeeId = EmployeeId.of(id);
        Employee employee = employeeRepository.findById(employeeId);
        if (employee == null) {
            throw new IllegalArgumentException("Employee not found: " + id);
        }
        return employee;
    }

    private EmployeeResponse toResponse(Employee employee) {
        EmployeeResponse response = new EmployeeResponse();
        response.setId(employee.getId().value());
        response.setName(employee.getName());
        response.setGender(employee.getGender());
        response.setPhone(employee.getPhone());
        response.setEmail(employee.getEmail());
        response.setStatus(employee.getStatus());
        response.setHireDate(employee.getHireDate());
        response.setDepartmentId(employee.getDepartmentId().value());
        response.setPositionId(employee.getPositionId().value());
        response.setSkills(employee.getSkills().stream()
                .map(s -> new SkillResponse(s.name(), s.level(), s.certifiedDate()))
                .collect(Collectors.toList()));
        response.setCreatedAt(employee.getCreatedAt());
        response.setUpdatedAt(employee.getUpdatedAt());
        return response;
    }

    private AvailableEmployeeResponse toAvailableResponse(Employee employee) {
        Set<String> skillNames = employee.getSkills().stream()
                .map(Skill::name)
                .collect(Collectors.toSet());

        return new AvailableEmployeeResponse(
                employee.getId().value(),
                employee.getName(),
                employee.getDepartmentId().value(),
                null, // departmentName - 需要从Org上下文获取
                employee.getPositionId().value(),
                null, // positionName - 需要从Org上下文获取
                skillNames
        );
    }
}
