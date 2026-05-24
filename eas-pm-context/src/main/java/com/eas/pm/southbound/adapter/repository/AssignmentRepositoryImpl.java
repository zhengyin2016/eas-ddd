package com.eas.pm.southbound.adapter.repository;

import com.eas.pm.domain.assignment.Assignment;
import com.eas.pm.domain.assignment.AssignmentId;
import com.eas.pm.domain.assignment.AssignmentRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Assignment仓储实现
 */
@Repository
public class AssignmentRepositoryImpl implements AssignmentRepository {

    private final AssignmentMapper assignmentMapper;

    public AssignmentRepositoryImpl(AssignmentMapper assignmentMapper) {
        this.assignmentMapper = assignmentMapper;
    }

    @Override
    public Assignment save(Assignment assignment) {
        if (assignment.id() == null) {
            assignmentMapper.insert(assignment);
        } else {
            int updated = assignmentMapper.update(assignment);
            if (updated == 0) {
                throw new org.springframework.orm.OptimisticLockingFailureException(
                    "Assignment update failed due to concurrent modification"
                );
            }
        }
        return assignment;
    }

    @Override
    public Optional<Assignment> findById(AssignmentId id) {
        AssignmentDO assignmentDO = assignmentMapper.findById(id.value());
        if (assignmentDO == null) {
            return Optional.empty();
        }

        Assignment assignment = new Assignment(
            AssignmentId.of(assignmentDO.id()),
            assignmentDO.projectId(),
            assignmentDO.employeeId(),
            assignmentDO.role(),
            assignmentDO.allocation(),
            assignmentDO.startDate(),
            assignmentDO.endDate()
        );

        if (assignmentDO.released()) {
            assignment.release();
        }

        assignment.setVersion(assignmentDO.version());
        return Optional.of(assignment);
    }

    @Override
    public List<Assignment> findByProjectId(String projectId) {
        List<AssignmentDO> assignmentDOs = assignmentMapper.findByProjectId(projectId);
        return assignmentDOs.stream()
            .map(this::toAssignment)
            .toList();
    }

    @Override
    public List<Assignment> findByEmployeeId(String employeeId) {
        List<AssignmentDO> assignmentDOs = assignmentMapper.findByEmployeeId(employeeId);
        return assignmentDOs.stream()
            .map(this::toAssignment)
            .toList();
    }

    @Override
    public List<Assignment> findActiveByEmployeeIdAndDateRange(String employeeId, LocalDate startDate, LocalDate endDate) {
        List<AssignmentDO> assignmentDOs = assignmentMapper.findActiveByEmployeeIdAndDateRange(employeeId, startDate, endDate);
        return assignmentDOs.stream()
            .map(this::toAssignment)
            .toList();
    }

    @Override
    public void delete(AssignmentId id) {
        assignmentMapper.deleteById(id.value());
    }

    private Assignment toAssignment(AssignmentDO assignmentDO) {
        Assignment assignment = new Assignment(
            AssignmentId.of(assignmentDO.id()),
            assignmentDO.projectId(),
            assignmentDO.employeeId(),
            assignmentDO.role(),
            assignmentDO.allocation(),
            assignmentDO.startDate(),
            assignmentDO.endDate()
        );

        if (assignmentDO.released()) {
            assignment.release();
        }

        assignment.setVersion(assignmentDO.version());
        return assignment;
    }
}
