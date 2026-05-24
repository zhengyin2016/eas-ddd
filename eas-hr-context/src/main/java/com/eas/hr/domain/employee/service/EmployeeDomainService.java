package com.eas.hr.domain.employee.service;

import com.eas.hr.domain.employee.Employee;
import com.eas.hr.domain.employee.EmployeeRepository;
import com.eas.hr.domain.employee.Skill;

import java.util.Objects;

/**
 * 员工领域服务
 * <p>
 * 处理涉及多个员工或需要资源库参与的领域逻辑。
 * 领域服务是无状态的，所有状态从聚合根或资源库获取。
 * </p>
 */
public class EmployeeDomainService {

    private final EmployeeRepository employeeRepository;

    public EmployeeDomainService(EmployeeRepository employeeRepository) {
        this.employeeRepository = Objects.requireNonNull(employeeRepository, "Employee repository cannot be null");
    }

    /**
     * 验证身份证号唯一性
     *
     * @param idCard 身份证号
     * @return true如果身份证号可用
     * @throws IllegalArgumentException 如果身份证号已存在
     */
    public boolean validateIdCardUnique(String idCard) {
        if (idCard == null || idCard.isBlank()) {
            throw new IllegalArgumentException("ID card cannot be null or blank");
        }
        return !employeeRepository.existsByIdCard(idCard);
    }

    /**
     * 验证手机号唯一性
     *
     * @param phone 手机号
     * @return true如果手机号可用
     * @throws IllegalArgumentException 如果手机号已存在
     */
    public boolean validatePhoneUnique(String phone) {
        if (phone == null || phone.isBlank()) {
            throw new IllegalArgumentException("Phone cannot be null or blank");
        }
        return !employeeRepository.existsByPhone(phone);
    }

    /**
     * 验证身份证号格式
     *
     * @param idCard 身份证号
     * @return true如果格式正确
     */
    public boolean validateIdCardFormat(String idCard) {
        if (idCard == null || idCard.isBlank()) {
            return false;
        }
        // 简单验证：18位数字或17位数字+X
        return idCard.matches("\\d{17}[0-9Xx]");
    }

    /**
     * 验证手机号格式
     *
     * @param phone 手机号
     * @return true如果格式正确
     */
    public boolean validatePhoneFormat(String phone) {
        if (phone == null || phone.isBlank()) {
            return false;
        }
        // 中国手机号：1开头，11位数字
        return phone.matches("1\\d{10}");
    }

    /**
     * 验证邮箱格式
     *
     * @param email 邮箱
     * @return true如果格式正确
     */
    public boolean validateEmailFormat(String email) {
        if (email == null || email.isBlank()) {
            return false;
        }
        // 简单邮箱格式验证
        return email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    }

    /**
     * 计算员工工龄（年）
     *
     * @param employee 员工
     * @return 工龄（年）
     */
    public int calculateYearsOfService(Employee employee) {
        if (employee == null || employee.getHireDate() == null) {
            return 0;
        }
        return java.time.Period.between(employee.getHireDate(), java.time.LocalDate.now()).getYears();
    }

    /**
     * 检查员工是否有指定技能
     *
     * @param employee  员工
     * @param skillName 技能名称
     * @return true如果有该技能
     */
    public boolean hasSkill(Employee employee, String skillName) {
        if (employee == null || skillName == null) {
            return false;
        }
        return employee.getSkills().stream()
                .anyMatch(skill -> skill.name().equals(skillName));
    }

    /**
     * 检查员工技能等级是否达到要求
     *
     * @param employee       员工
     * @param skillName      技能名称
     * @param requiredLevel 要求等级
     * @return true如果达到要求
     */
    public boolean meetsSkillLevel(Employee employee, String skillName, com.eas.hr.domain.employee.SkillLevel requiredLevel) {
        if (employee == null || skillName == null || requiredLevel == null) {
            return false;
        }
        return employee.getSkills().stream()
                .filter(skill -> skill.name().equals(skillName))
                .anyMatch(skill -> skill.level().ordinal() >= requiredLevel.ordinal());
    }
}
