package com.eas.pm.message;

import java.math.BigDecimal;

/**
 * 可用员工DTO（PM上下文对HR员工的表示）
 * 这是ACL的输出，不是HR的领域模型
 */
public record AvailableEmployeeDTO(
    String employeeId,
    String name,
    BigDecimal availableRatio,
    String department
) {}
