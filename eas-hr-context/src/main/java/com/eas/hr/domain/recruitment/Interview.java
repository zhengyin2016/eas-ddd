package com.eas.hr.domain.recruitment;

import com.eas.common.ddd.Entity;
import com.eas.common.ddd.ValueObject;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * 面试实体
 * <p>
 * 面试是招聘聚合内的实体，由招聘需求聚合根管理。
 * </p>
 */
public class Interview extends Entity<InterviewId> {

    private String requirementId;
    private String candidateName;
    private String candidatePhone;
    private LocalDateTime interviewTime;
    private String interviewer;
    private InterviewResult result;
    private String feedback;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Interview(InterviewId id, String requirementId, String candidateName,
                     LocalDateTime interviewTime, String interviewer) {
        super(id);
        this.requirementId = Objects.requireNonNull(requirementId, "Requirement ID cannot be null");
        this.candidateName = Objects.requireNonNull(candidateName, "Candidate name cannot be null");
        this.interviewTime = Objects.requireNonNull(interviewTime, "Interview time cannot be null");
        this.interviewer = Objects.requireNonNull(interviewer, "Interviewer cannot be null");
        this.result = InterviewResult.PENDING;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public void reschedule(LocalDateTime newTime, String newInterviewer) {
        if (this.result != InterviewResult.PENDING) {
            throw new IllegalStateException("Cannot reschedule completed interview");
        }
        this.interviewTime = Objects.requireNonNull(newTime, "Interview time cannot be null");
        this.interviewer = Objects.requireNonNull(newInterviewer, "Interviewer cannot be null");
        this.updatedAt = LocalDateTime.now();
    }

    public void complete(InterviewResult result, String feedback) {
        if (this.result != InterviewResult.PENDING) {
            throw new IllegalStateException("Interview already completed");
        }
        this.result = Objects.requireNonNull(result, "Result cannot be null");
        this.feedback = feedback;
        this.updatedAt = LocalDateTime.now();
    }

    public void updateCandidateInfo(String name, String phone) {
        if (this.result != InterviewResult.PENDING) {
            throw new IllegalStateException("Cannot update candidate info for completed interview");
        }
        this.candidateName = Objects.requireNonNull(name, "Candidate name cannot be null");
        this.candidatePhone = phone;
        this.updatedAt = LocalDateTime.now();
    }

    // Getters

    public String getRequirementId() {
        return requirementId;
    }

    public String getCandidateName() {
        return candidateName;
    }

    public String getCandidatePhone() {
        return candidatePhone;
    }

    public LocalDateTime getInterviewTime() {
        return interviewTime;
    }

    public String getInterviewer() {
        return interviewer;
    }

    public InterviewResult getResult() {
        return result;
    }

    public String getFeedback() {
        return feedback;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public boolean isPending() {
        return result == InterviewResult.PENDING;
    }

    public boolean isPassed() {
        return result == InterviewResult.PASSED;
    }

    public boolean isFailed() {
        return result == InterviewResult.FAILED;
    }
}
