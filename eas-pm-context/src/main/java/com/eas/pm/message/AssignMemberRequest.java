package com.eas.pm.message;

import com.eas.pm.domain.assignment.AssignmentRole;

import java.time.LocalDate;

/**
 * 分配成员请求DTO
 */
public record AssignMemberRequest(
    String projectId,
    String employeeId,
    AssignmentRole role,
    int allocation,
    LocalDate startDate,
    LocalDate endDate
) {}
