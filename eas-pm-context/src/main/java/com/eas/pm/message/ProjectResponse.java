package com.eas.pm.message;

import com.eas.pm.domain.project.ProjectStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * 项目响应DTO
 */
public record ProjectResponse(
    String id,
    String name,
    String customerId,
    String contractId,
    String pmId,
    ProjectStatus status,
    BigDecimal budget,
    LocalDate startDate,
    LocalDate endDate,
    List<TaskSummary> tasks,
    List<MilestoneSummary> milestones
) {
    public record TaskSummary(
        String id,
        String name,
        String assigneeId,
        String status,
        int estimatedHours,
        int actualHours
    ) {}

    public record MilestoneSummary(
        String id,
        String name,
        LocalDate plannedDate,
        LocalDate actualDate,
        boolean completed
    ) {}
}
