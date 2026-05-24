package com.eas.pm.southbound.adapter.repository;

import com.eas.pm.domain.assignment.AssignmentRole;

import java.time.LocalDate;

/**
 * Assignment数据对象（DO）
 */
public record AssignmentDO(
    String id,
    String projectId,
    String employeeId,
    AssignmentRole role,
    int allocation,
    LocalDate startDate,
    LocalDate endDate,
    boolean released,
    LocalDate releasedAt,
    int version
) {}
