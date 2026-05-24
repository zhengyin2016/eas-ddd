package com.eas.hr.domain.employee;

import com.eas.common.ddd.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 员工资源库接口
 * <p>
 * 定义在领域层，实现在基础设施层。
 * 这是菱形对称架构中的端口(Port)。
 * </p>
 */
public interface EmployeeRepository extends Repository<Employee, EmployeeId> {

    /**
     * 根据身份证号查找员工
     *
     * @param idCard 身份证号
     * @return 员工对象
     */
    Optional<Employee> findByIdCard(String idCard);

    /**
     * 根据手机号查找员工
     *
     * @param phone 手机号
     * @return 员工对象
     */
    Optional<Employee> findByPhone(String phone);

    /**
     * 检查身份证号是否存在
     *
     * @param idCard 身份证号
     * @return true如果存在
     */
    boolean existsByIdCard(String idCard);

    /**
     * 检查手机号是否存在
     *
     * @param phone 手机号
     * @return true如果存在
     */
    boolean existsByPhone(String phone);

    /**
     * 根据部门ID查找所有员工
     *
     * @param departmentId 部门ID
     * @return 员工列表
     */
    List<Employee> findByDepartmentId(String departmentId);

    /**
     * 根据状态查找员工
     *
     * @param status 员工状态
     * @return 员工列表
     */
    List<Employee> findByStatus(EmployeeStatus status);

    /**
     * 查询可用员工
     * <p>
     * 可用员工指正式员工且未离职的员工。
     * </p>
     *
     * @param departmentId 部门ID（可选）
     * @param positionId   岗位ID（可选）
     * @param skillName    技能名称（可选）
     * @return 可用员工列表
     */
    List<Employee> findAvailableEmployees(String departmentId, String positionId, String skillName);
}
