package com.eas.pm.message;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 创建项目请求DTO
 */
public record CreateProjectRequest(
    String name,
    String customerId,
    String contractId,
    String pmId,
    BigDecimal budget,
    LocalDate startDate,
    LocalDate endDate
) {}
