package com.eas.hr.domain.recruitment;

import com.eas.common.ddd.AggregateRoot;
import com.eas.hr.domain.employee.DepartmentId;
import com.eas.hr.domain.employee.PositionId;
import com.eas.hr.domain.event.RecruitmentApprovedEvent;
import com.eas.hr.domain.event.RecruitmentSubmittedEvent;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 招聘需求聚合根
 */
public class RecruitmentRequirement extends AggregateRoot<RecruitmentId> {

    private String title;
    private DepartmentId departmentId;
    private PositionId positionId;
    private int count;
    private String description;
    private String requirements;
    private RecruitmentStatus status;
    private String approver;
    private String rejectionReason;
    private LocalDateTime approvedAt;
    private List<Interview> interviews;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;

    private RecruitmentRequirement(RecruitmentId id, String title, DepartmentId departmentId,
                                   PositionId positionId, int count, String description, String requirements,
                                   String createdBy) {
        super(id);
        this.title = Objects.requireNonNull(title, "Title cannot be null");
        this.departmentId = Objects.requireNonNull(departmentId, "Department ID cannot be null");
        this.positionId = Objects.requireNonNull(positionId, "Position ID cannot be null");
        if (count <= 0) {
            throw new IllegalArgumentException("Count must be positive");
        }
        this.count = count;
        this.description = description;
        this.requirements = requirements;
        this.createdBy = Objects.requireNonNull(createdBy, "Created by cannot be null");
        this.status = RecruitmentStatus.DRAFT;
        this.interviews = new ArrayList<>();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public static RecruitmentRequirement create(String title, DepartmentId departmentId,
                                                PositionId positionId, int count, String description,
                                                String requirements, String createdBy) {
        return new RecruitmentRequirement(RecruitmentId.generate(), title, departmentId,
                positionId, count, description, requirements, createdBy);
    }

    public static RecruitmentRequirement restore(RecruitmentId id, String title, DepartmentId departmentId,
                                                 PositionId positionId, int count, String description,
                                                 String requirements, RecruitmentStatus status, String approver,
                                                 String rejectionReason, LocalDateTime approvedAt,
                                                 List<Interview> interviews, LocalDateTime createdAt,
                                                 LocalDateTime updatedAt, String createdBy) {
        RecruitmentRequirement requirement = new RecruitmentRequirement(id, title, departmentId,
                positionId, count, description, requirements, createdBy);
        requirement.status = status;
        requirement.approver = approver;
        requirement.rejectionReason = rejectionReason;
        requirement.approvedAt = approvedAt;
        requirement.interviews = new ArrayList<>(interviews);
        requirement.createdAt = createdAt;
        requirement.updatedAt = updatedAt;
        return requirement;
    }

    public void submit() {
        if (status != RecruitmentStatus.DRAFT) {
            throw new IllegalStateException("Only DRAFT status can be submitted");
        }
        this.status = RecruitmentStatus.PENDING;
        this.updatedAt = LocalDateTime.now();
        addDomainEvent(new RecruitmentSubmittedEvent(getId().value(), title, departmentId.value()));
    }

    public void approve(String approver) {
        if (status != RecruitmentStatus.PENDING) {
            throw new IllegalStateException("Only PENDING status can be approved");
        }
        this.status = RecruitmentStatus.APPROVED;
        this.approver = Objects.requireNonNull(approver, "Approver cannot be null");
        this.approvedAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        addDomainEvent(new RecruitmentApprovedEvent(getId().value(), title, approver));
    }

    public void reject(String approver, String reason) {
        if (status != RecruitmentStatus.PENDING) {
            throw new IllegalStateException("Only PENDING status can be rejected");
        }
        this.status = RecruitmentStatus.REJECTED;
        this.approver = Objects.requireNonNull(approver, "Approver cannot be null");
        this.rejectionReason = reason;
        this.updatedAt = LocalDateTime.now();
    }

    public void cancel() {
        if (status == RecruitmentStatus.FULFILLED || status == RecruitmentStatus.CANCELLED) {
            throw new IllegalStateException("Cannot cancel fulfilled or already cancelled requirement");
        }
        this.status = RecruitmentStatus.CANCELLED;
        this.updatedAt = LocalDateTime.now();
    }

    public void markAsFulfilled() {
        if (status != RecruitmentStatus.APPROVED) {
            throw new IllegalStateException("Only APPROVED status can be marked as fulfilled");
        }
        this.status = RecruitmentStatus.FULFILLED;
        this.updatedAt = LocalDateTime.now();
    }

    public Interview scheduleInterview(String candidateName, String candidatePhone,
                                        LocalDateTime interviewTime, String interviewer) {
        if (status != RecruitmentStatus.APPROVED) {
            throw new IllegalStateException("Only APPROVED requirement can schedule interviews");
        }
        Interview interview = new Interview(InterviewId.generate(), getId().value(),
                candidateName, interviewTime, interviewer);
        interview.updateCandidateInfo(candidateName, candidatePhone);
        interviews.add(interview);
        this.updatedAt = LocalDateTime.now();
        return interview;
    }

    public void cancelInterview(InterviewId interviewId) {
        interviews.removeIf(i -> i.getId().equals(interviewId));
        this.updatedAt = LocalDateTime.now();
    }

    public void updateInfo(String title, int count, String description, String requirements) {
        if (status != RecruitmentStatus.DRAFT && status != RecruitmentStatus.REJECTED) {
            throw new IllegalStateException("Only DRAFT or REJECTED status can be updated");
        }
        this.title = Objects.requireNonNull(title, "Title cannot be null");
        if (count <= 0) {
            throw new IllegalArgumentException("Count must be positive");
        }
        this.count = count;
        this.description = description;
        this.requirements = requirements;
        this.updatedAt = LocalDateTime.now();
    }

    public int getPassedInterviewCount() {
        return (int) interviews.stream().filter(Interview::isPassed).count();
    }

    public boolean canScheduleMoreInterviews() {
        return getPassedInterviewCount() < count;
    }

    // Getters

    public String getTitle() {
        return title;
    }

    public DepartmentId getDepartmentId() {
        return departmentId;
    }

    public PositionId getPositionId() {
        return positionId;
    }

    public int getCount() {
        return count;
    }

    public String getDescription() {
        return description;
    }

    public String getRequirements() {
        return requirements;
    }

    public RecruitmentStatus getStatus() {
        return status;
    }

    public String getApprover() {
        return approver;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public LocalDateTime getApprovedAt() {
        return approvedAt;
    }

    public List<Interview> getInterviews() {
        return new ArrayList<>(interviews);
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public String getCreatedBy() {
        return createdBy;
    }
}
