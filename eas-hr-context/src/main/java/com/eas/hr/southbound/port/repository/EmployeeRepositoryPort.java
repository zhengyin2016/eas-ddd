package com.eas.hr.southbound.port.repository;

import com.eas.hr.domain.employee.Employee;
import com.eas.hr.domain.employee.EmployeeId;
import com.eas.hr.domain.employee.EmployeeStatus;

import java.util.List;
import java.util.Optional;

/**
 * 员工资源库端口
 * <p>
 * 这是菱形对称架构中的南向网关 - 端口(Port)。
 * 端口定义在领域层（通过继承Repository接口），适配器实现在基础设施层。
 * </p>
 */
public interface EmployeeRepositoryPort extends com.eas.hr.domain.employee.EmployeeRepository {

    /**
     * 根据状态查找员工
     *
     * @param status 员工状态
     * @return 员工列表
     */
    List<Employee> findByStatus(EmployeeStatus status);
}
