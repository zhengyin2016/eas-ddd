package com.eas.hr.domain.training;

import com.eas.common.ddd.Entity;
import com.eas.hr.domain.employee.EmployeeId;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * 培训报名实体
 * <p>
 * 报名是培训聚合内的实体，由培训计划聚合根管理。
 * </p>
 */
public class TrainingEnrollment extends Entity<EnrollmentId> {

    private TrainingId planId;
    private EmployeeId employeeId;
    private EnrollmentStatus status;
    private LocalDateTime enrolledAt;
    private LocalDateTime checkInTime;
    private BigDecimal completionRate;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public TrainingEnrollment(EnrollmentId id, TrainingId planId, EmployeeId employeeId) {
        super(id);
        this.planId = Objects.requireNonNull(planId, "Plan ID cannot be null");
        this.employeeId = Objects.requireNonNull(employeeId, "Employee ID cannot be null");
        this.status = EnrollmentStatus.PENDING;
        this.enrolledAt = LocalDateTime.now();
        this.completionRate = BigDecimal.ZERO;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public void confirm() {
        if (status != EnrollmentStatus.PENDING) {
            throw new IllegalStateException("Only PENDING status can be confirmed");
        }
        this.status = EnrollmentStatus.CONFIRMED;
        this.updatedAt = LocalDateTime.now();
    }

    public void checkIn() {
        if (status == EnrollmentStatus.CANCELLED) {
            throw new IllegalStateException("Cannot check in cancelled enrollment");
        }
        if (checkInTime != null) {
            throw new IllegalStateException("Already checked in");
        }
        this.checkInTime = LocalDateTime.now();
        this.status = EnrollmentStatus.ATTENDED;
        this.updatedAt = LocalDateTime.now();
    }

    public void updateCompletionRate(BigDecimal rate) {
        Objects.requireNonNull(rate, "Completion rate cannot be null");
        if (rate.compareTo(BigDecimal.ZERO) < 0 || rate.compareTo(new BigDecimal("100")) > 0) {
            throw new IllegalArgumentException("Completion rate must be between 0 and 100");
        }
        this.completionRate = rate;
        this.updatedAt = LocalDateTime.now();
        if (rate.compareTo(new BigDecimal("100")) >= 0 && status == EnrollmentStatus.ATTENDED) {
            this.status = EnrollmentStatus.COMPLETED;
        }
    }

    public void complete() {
        if (status != EnrollmentStatus.ATTENDED) {
            throw new IllegalStateException("Only ATTENDED status can be completed");
        }
        this.status = EnrollmentStatus.COMPLETED;
        this.completionRate = new BigDecimal("100");
        this.updatedAt = LocalDateTime.now();
    }

    public void cancel() {
        if (status == EnrollmentStatus.COMPLETED) {
            throw new IllegalStateException("Cannot cancel completed enrollment");
        }
        this.status = EnrollmentStatus.CANCELLED;
        this.updatedAt = LocalDateTime.now();
    }

    public void addNotes(String notes) {
        this.notes = notes;
        this.updatedAt = LocalDateTime.now();
    }

    // Getters

    public TrainingId getPlanId() {
        return planId;
    }

    public EmployeeId getEmployeeId() {
        return employeeId;
    }

    public EnrollmentStatus getStatus() {
        return status;
    }

    public LocalDateTime getEnrolledAt() {
        return enrolledAt;
    }

    public LocalDateTime getCheckInTime() {
        return checkInTime;
    }

    public BigDecimal getCompletionRate() {
        return completionRate;
    }

    public String getNotes() {
        return notes;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public boolean isCheckedIn() {
        return checkInTime != null;
    }

    public boolean isCompleted() {
        return status == EnrollmentStatus.COMPLETED;
    }
}
