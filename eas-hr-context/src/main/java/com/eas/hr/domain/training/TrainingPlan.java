package com.eas.hr.domain.training;

import com.eas.common.ddd.AggregateRoot;
import com.eas.hr.domain.employee.EmployeeId;
import com.eas.hr.domain.event.TrainingPublishedEvent;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 培训计划聚合根
 */
public class TrainingPlan extends AggregateRoot<TrainingId> {

    private String name;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
    private int capacity;
    private TrainingStatus status;
    private String instructor;
    private String location;
    private List<TrainingEnrollment> enrollments;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;

    private TrainingPlan(TrainingId id, String name, String description,
                        LocalDate startDate, LocalDate endDate, int capacity, String createdBy) {
        super(id);
        this.name = Objects.requireNonNull(name, "Name cannot be null");
        this.description = description;
        this.startDate = Objects.requireNonNull(startDate, "Start date cannot be null");
        this.endDate = Objects.requireNonNull(endDate, "End date cannot be null");
        if (endDate.isBefore(startDate)) {
            throw new IllegalArgumentException("End date cannot be before start date");
        }
        if (capacity <= 0) {
            throw new IllegalArgumentException("Capacity must be positive");
        }
        this.capacity = capacity;
        this.createdBy = Objects.requireNonNull(createdBy, "Created by cannot be null");
        this.status = TrainingStatus.DRAFT;
        this.enrollments = new ArrayList<>();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public static TrainingPlan create(String name, String description,
                                      LocalDate startDate, LocalDate endDate, int capacity, String createdBy) {
        return new TrainingPlan(TrainingId.generate(), name, description, startDate, endDate, capacity, createdBy);
    }

    public static TrainingPlan restore(TrainingId id, String name, String description,
                                       LocalDate startDate, LocalDate endDate, int capacity, TrainingStatus status,
                                       String instructor, String location, List<TrainingEnrollment> enrollments,
                                       LocalDateTime createdAt, LocalDateTime updatedAt, String createdBy) {
        TrainingPlan plan = new TrainingPlan(id, name, description, startDate, endDate, capacity, createdBy);
        plan.status = status;
        plan.instructor = instructor;
        plan.location = location;
        plan.enrollments = new ArrayList<>(enrollments);
        plan.createdAt = createdAt;
        plan.updatedAt = updatedAt;
        return plan;
    }

    public void publish() {
        if (status != TrainingStatus.DRAFT) {
            throw new IllegalStateException("Only DRAFT status can be published");
        }
        this.status = TrainingStatus.PUBLISHED;
        this.updatedAt = LocalDateTime.now();
        addDomainEvent(new TrainingPublishedEvent(getId().value(), name, capacity));
    }

    public void start() {
        if (status != TrainingStatus.PUBLISHED) {
            throw new IllegalStateException("Only PUBLISHED status can be started");
        }
        this.status = TrainingStatus.IN_PROGRESS;
        this.updatedAt = LocalDateTime.now();
    }

    public void complete() {
        if (status != TrainingStatus.IN_PROGRESS) {
            throw new IllegalStateException("Only IN_PROGRESS status can be completed");
        }
        this.status = TrainingStatus.COMPLETED;
        this.updatedAt = LocalDateTime.now();
    }

    public void cancel() {
        if (status == TrainingStatus.COMPLETED || status == TrainingStatus.CANCELLED) {
            throw new IllegalStateException("Cannot cancel completed or already cancelled training");
        }
        this.status = TrainingStatus.CANCELLED;
        this.updatedAt = LocalDateTime.now();
    }

    public TrainingEnrollment enroll(EmployeeId employeeId) {
        if (status != TrainingStatus.PUBLISHED) {
            throw new IllegalStateException("Only PUBLISHED training can be enrolled");
        }
        if (!hasCapacity()) {
            throw new IllegalStateException("Training is full");
        }
        if (isEnrolled(employeeId)) {
            throw new IllegalStateException("Employee already enrolled");
        }

        TrainingEnrollment enrollment = new TrainingEnrollment(EnrollmentId.generate(), getId(), employeeId);
        enrollments.add(enrollment);
        this.updatedAt = LocalDateTime.now();
        return enrollment;
    }

    public void cancelEnrollment(EnrollmentId enrollmentId) {
        if (status == TrainingStatus.COMPLETED) {
            throw new IllegalStateException("Cannot cancel enrollment for completed training");
        }
        TrainingEnrollment enrollment = findEnrollment(enrollmentId)
                .orElseThrow(() -> new IllegalArgumentException("Enrollment not found"));
        enrollment.cancel();
        this.updatedAt = LocalDateTime.now();
    }

    public void checkInEnrollment(EnrollmentId enrollmentId) {
        TrainingEnrollment enrollment = findEnrollment(enrollmentId)
                .orElseThrow(() -> new IllegalArgumentException("Enrollment not found"));
        enrollment.checkIn();
        this.updatedAt = LocalDateTime.now();
    }

    public void updateInfo(String name, String description, LocalDate startDate, LocalDate endDate, int capacity) {
        if (status != TrainingStatus.DRAFT) {
            throw new IllegalStateException("Only DRAFT status can be updated");
        }
        this.name = Objects.requireNonNull(name, "Name cannot be null");
        this.description = description;
        this.startDate = Objects.requireNonNull(startDate, "Start date cannot be null");
        this.endDate = Objects.requireNonNull(endDate, "End date cannot be null");
        if (endDate.isBefore(startDate)) {
            throw new IllegalArgumentException("End date cannot be before start date");
        }
        if (capacity <= 0) {
            throw new IllegalArgumentException("Capacity must be positive");
        }
        this.capacity = capacity;
        this.updatedAt = LocalDateTime.now();
    }

    public void setInstructor(String instructor) {
        this.instructor = instructor;
        this.updatedAt = LocalDateTime.now();
    }

    public void setLocation(String location) {
        this.location = location;
        this.updatedAt = LocalDateTime.now();
    }

    // Helper methods

    public boolean hasCapacity() {
        return getActiveEnrollmentCount() < capacity;
    }

    public boolean isEnrolled(EmployeeId employeeId) {
        return enrollments.stream()
                .anyMatch(e -> e.getEmployeeId().equals(employeeId)
                        && e.getStatus() != EnrollmentStatus.CANCELLED);
    }

    public Optional<TrainingEnrollment> findEnrollment(EnrollmentId enrollmentId) {
        return enrollments.stream()
                .filter(e -> e.getId().equals(enrollmentId))
                .findFirst();
    }

    public int getActiveEnrollmentCount() {
        return (int) enrollments.stream()
                .filter(e -> e.getStatus() != EnrollmentStatus.CANCELLED)
                .count();
    }

    public int getCheckedInCount() {
        return (int) enrollments.stream()
                .filter(TrainingEnrollment::isCheckedIn)
                .count();
    }

    public int getCompletedCount() {
        return (int) enrollments.stream()
                .filter(TrainingEnrollment::isCompleted)
                .count();
    }

    // Getters

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public int getCapacity() {
        return capacity;
    }

    public TrainingStatus getStatus() {
        return status;
    }

    public String getInstructor() {
        return instructor;
    }

    public String getLocation() {
        return location;
    }

    public List<TrainingEnrollment> getEnrollments() {
        return new ArrayList<>(enrollments);
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
