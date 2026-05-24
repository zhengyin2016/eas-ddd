package com.eas.pm.southbound.port.client;

import com.eas.pm.message.AvailableEmployeeDTO;

import java.time.LocalDate;
import java.util.List;

/**
 * PM调用HR上下文的防腐层端口
 */
public interface HRClientPort {

    /**
     * 查询单个员工信息
     */
    AvailableEmployeeDTO queryEmployee(String employeeId);

    /**
     * 查询可用员工列表
     */
    List<AvailableEmployeeDTO> queryAvailableEmployees(LocalDate startDate, LocalDate endDate);

    /**
     * 检查员工是否可用
     */
    boolean isEmployeeAvailable(String employeeId, LocalDate startDate, LocalDate endDate);
}
