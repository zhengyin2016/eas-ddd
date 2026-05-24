package com.eas.pm.northbound.appservice;

import com.eas.pm.domain.assignment.*;
import com.eas.pm.domain.project.ProjectRepository;
import com.eas.pm.message.AssignMemberRequest;
import com.eas.pm.message.AvailableEmployeeDTO;
import com.eas.pm.southbound.port.client.HRClientPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * 人员分配应用服务
 */
@Service
public class AssignmentAppService {

    private final AssignmentRepository assignmentRepository;
    private final AssignmentDomainService assignmentDomainService;
    private final ProjectRepository projectRepository;
    private final HRClientPort hrClientPort;

    public AssignmentAppService(AssignmentRepository assignmentRepository,
                                AssignmentDomainService assignmentDomainService,
                                ProjectRepository projectRepository,
                                HRClientPort hrClientPort) {
        this.assignmentRepository = assignmentRepository;
        this.assignmentDomainService = assignmentDomainService;
        this.projectRepository = projectRepository;
        this.hrClientPort = hrClientPort;
    }

    @Transactional
    public void assignMember(AssignMemberRequest request) {
        // 验证员工可用性
        boolean available = hrClientPort.isEmployeeAvailable(
            request.employeeId(),
            request.startDate(),
            request.endDate()
        );

        if (!available) {
            throw new IllegalStateException("员工在指定时间段不可用");
        }

        // 检查分配冲突
        boolean hasConflict = assignmentDomainService.checkAllocationConflict(
            request.employeeId(),
            request.projectId(),
            request.startDate(),
            request.endDate()
        );

        if (hasConflict) {
            throw new IllegalStateException("员工在该项目上已存在时间重叠的分配");
        }

        Assignment assignment = new Assignment(
            AssignmentId.generate(),
            request.projectId(),
            request.employeeId(),
            request.role(),
            request.allocation(),
            request.startDate(),
            request.endDate()
        );

        assignmentRepository.save(assignment);
    }

    @Transactional
    public void releaseMember(String assignmentId) {
        Assignment assignment = findAssignment(assignmentId);
        assignment.release();
        assignmentRepository.save(assignment);
    }

    @Transactional
    public void updateAllocation(String assignmentId, int newAllocation) {
        Assignment assignment = findAssignment(assignmentId);
        assignment.updateAllocation(newAllocation);
        assignmentRepository.save(assignment);
    }

    @Transactional(readOnly = true)
    public List<AvailableEmployeeDTO> queryAvailableResources(String projectId) {
        // 获取项目时间段
        var project = projectRepository.findById(projectId)
            .orElseThrow(() -> new IllegalArgumentException("Project not found: " + projectId));

        // 查询可用员工
        return hrClientPort.queryAvailableEmployees(
            project.startDate(),
            project.endDate()
        );
    }

    private Assignment findAssignment(String assignmentId) {
        return assignmentRepository.findById(AssignmentId.of(assignmentId))
            .orElseThrow(() -> new IllegalArgumentException("Assignment not found: " + assignmentId));
    }
}
