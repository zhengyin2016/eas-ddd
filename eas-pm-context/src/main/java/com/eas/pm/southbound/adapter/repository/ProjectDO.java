package com.eas.pm.southbound.adapter.repository;

import com.eas.pm.domain.project.ProjectStatus;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Project数据对象（DO）
 */
public record ProjectDO(
    String id,
    String name,
    String customerId,
    String contractId,
    String pmId,
    ProjectStatus status,
    BigDecimal budget,
    LocalDate startDate,
    LocalDate endDate,
    int version
) {}
