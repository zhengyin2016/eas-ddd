package com.eas.pm.domain.assignment;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * 人员分配仓储端口
 */
public interface AssignmentRepository {

    Assignment save(Assignment assignment);

    Optional<Assignment> findById(AssignmentId id);

    List<Assignment> findByProjectId(String projectId);

    List<Assignment> findByEmployeeId(String employeeId);

    List<Assignment> findActiveByEmployeeIdAndDateRange(String employeeId, LocalDate startDate, LocalDate endDate);

    void delete(AssignmentId id);
}
