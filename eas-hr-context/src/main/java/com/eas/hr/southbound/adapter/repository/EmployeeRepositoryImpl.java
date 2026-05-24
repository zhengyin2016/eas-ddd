package com.eas.hr.southbound.adapter.repository;

import com.eas.hr.domain.employee.*;
import com.eas.hr.southbound.port.repository.EmployeeRepositoryPort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 员工资源库适配器实现
 * <p>
 * 这是菱形对称架构中的南向网关 - 适配器(Adapter)实现。
 * 负责将领域对象转换为持久化格式，并调用MyBatis Mapper进行数据库操作。
 * </p>
 */
@Component
public class EmployeeRepositoryImpl implements EmployeeRepositoryPort {

    private final EmployeeMapper employeeMapper;

    public EmployeeRepositoryImpl(EmployeeMapper employeeMapper) {
        this.employeeMapper = employeeMapper;
    }

    @Override
    @Transactional
    public Employee save(Employee aggregate) {
        // 检查是否是新员工
        boolean isNew = employeeMapper.findById(aggregate.getId().value()) == null;

        if (isNew) {
            employeeMapper.insert(aggregate);
        } else {
            employeeMapper.update(aggregate);
        }

        // 处理技能
        saveSkills(aggregate);

        return aggregate;
    }

    @Override
    public Employee findById(EmployeeId id) {
        EmployeeMapper.EmployeeDO employeeDO = employeeMapper.findById(id.value());
        if (employeeDO == null) {
            return null;
        }
        return toEmployee(employeeDO);
    }

    @Override
    public void deleteById(EmployeeId id) {
        // 删除员工技能
        employeeMapper.deleteSkill(id.value(), null);
        // 删除员工
        // employeeMapper.delete(id.value());
        // 实际上员工不应物理删除，而是标记为已离职
    }

    @Override
    public Optional<Employee> findByIdCard(String idCard) {
        EmployeeMapper.EmployeeDO employeeDO = employeeMapper.findByIdCard(idCard);
        return Optional.ofNullable(employeeDO).map(this::toEmployee);
    }

    @Override
    public Optional<Employee> findByPhone(String phone) {
        EmployeeMapper.EmployeeDO employeeDO = employeeMapper.findByPhone(phone);
        return Optional.ofNullable(employeeDO).map(this::toEmployee);
    }

    @Override
    public boolean existsByIdCard(String idCard) {
        return employeeMapper.findByIdCard(idCard) != null;
    }

    @Override
    public boolean existsByPhone(String phone) {
        return employeeMapper.findByPhone(phone) != null;
    }

    @Override
    public List<Employee> findByDepartmentId(String departmentId) {
        List<EmployeeMapper.EmployeeDO> employeeDOs = employeeMapper.findByDepartmentId(departmentId);
        return employeeDOs.stream()
                .map(this::toEmployee)
                .collect(Collectors.toList());
    }

    @Override
    public List<Employee> findByStatus(EmployeeStatus status) {
        List<EmployeeMapper.EmployeeDO> employeeDOs = employeeMapper.findByStatus(status.name());
        return employeeDOs.stream()
                .map(this::toEmployee)
                .collect(Collectors.toList());
    }

    @Override
    public List<Employee> findAvailableEmployees(String departmentId, String positionId, String skillName) {
        List<EmployeeMapper.EmployeeDO> employeeDOs = employeeMapper.findAvailableEmployees(
                departmentId, positionId, skillName);
        return employeeDOs.stream()
                .map(this::toEmployee)
                .collect(Collectors.toList());
    }

    private void saveSkills(Employee employee) {
        // 先删除所有技能
        employeeMapper.deleteSkill(employee.getId().value(), null);

        // 重新插入技能
        for (Skill skill : employee.getSkills()) {
            employeeMapper.insertSkill(
                    employee.getId().value(),
                    skill.name(),
                    skill.level().name(),
                    skill.certifiedDate()
            );
        }
    }

    private Employee toEmployee(EmployeeMapper.EmployeeDO employeeDO) {
        List<Skill> skills = employeeMapper.findSkillsByEmployeeId(employeeDO.getId());

        // 简化：状态变更历史从状态推断
        List<StatusChange> statusHistory = List.of(
                StatusChange.of(null, employeeDO.getStatus())
        );

        return Employee.restore(
                EmployeeId.of(employeeDO.getId()),
                employeeDO.getName(),
                employeeDO.getGender(),
                employeeDO.getIdCard(),
                employeeDO.getPhone(),
                employeeDO.getEmail(),
                employeeDO.getStatus(),
                employeeDO.getHireDate(),
                DepartmentId.of(employeeDO.getDepartmentId()),
                PositionId.of(employeeDO.getPositionId()),
                skills,
                statusHistory,
                employeeDO.getCreatedAt(),
                employeeDO.getUpdatedAt()
        );
    }
}
