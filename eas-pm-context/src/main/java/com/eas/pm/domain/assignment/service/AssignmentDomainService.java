package com.eas.pm.domain.assignment.service;

import com.eas.pm.domain.assignment.AssignmentRepository;
import com.eas.pm.southbound.port.client.HRClientPort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

/**
 * 人员分配领域服务
 */
@Service
public class AssignmentDomainService {

    private final AssignmentRepository assignmentRepository;
    private final HRClientPort hrClientPort;

    public AssignmentDomainService(AssignmentRepository assignmentRepository,
                                   HRClientPort hrClientPort) {
        this.assignmentRepository = assignmentRepository;
        this.hrClientPort = hrClientPort;
    }

    /**
     * 检查分配时间冲突
     */
    public boolean checkAllocationConflict(String employeeId, String projectId,
                                           LocalDate startDate, LocalDate endDate) {
        List<com.eas.pm.domain.assignment.Assignment> existingAssignments =
            assignmentRepository.findActiveByEmployeeIdAndDateRange(employeeId, startDate, endDate);

        return existingAssignments.stream()
            .anyMatch(a -> a.projectId().equals(projectId) && hasOverlap(a, startDate, endDate));
    }

    private boolean hasOverlap(com.eas.pm.domain.assignment.Assignment assignment,
                               LocalDate startDate, LocalDate endDate) {
        return !assignment.endDate().isBefore(startDate) && !assignment.startDate().isAfter(endDate);
    }
}
