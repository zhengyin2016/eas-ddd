package com.eas.pm.domain.project;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 项目聚合根
 */
public class Project {

    private final ProjectId id;
    private String name;
    private String customerId;
    private String contractId;
    private String pmId;
    private ProjectStatus status;
    private BigDecimal budget;
    private LocalDate startDate;
    private LocalDate endDate;
    private final List<Task> tasks;
    private final List<Milestone> milestones;
    private int version;

    private Project(Builder builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.customerId = builder.customerId;
        this.contractId = builder.contractId;
        this.pmId = builder.pmId;
        this.status = ProjectStatus.PREPARING;
        this.budget = builder.budget;
        this.startDate = builder.startDate;
        this.endDate = builder.endDate;
        this.tasks = new ArrayList<>();
        this.milestones = new ArrayList<>();
        this.version = 0;
    }

    public void approve() {
        if (this.status != ProjectStatus.PREPARING) {
            throw new IllegalStateException("只有准备中的项目才能审批");
        }
        this.status = ProjectStatus.APPROVED;
    }

    public void start() {
        if (this.status != ProjectStatus.APPROVED) {
            throw new IllegalStateException("只有已审批的项目才能启动");
        }
        this.status = ProjectStatus.IN_PROGRESS;
    }

    public void suspend() {
        if (this.status != ProjectStatus.IN_PROGRESS) {
            throw new IllegalStateException("只有进行中的项目才能暂停");
        }
        this.status = ProjectStatus.SUSPENDED;
    }

    public void resume() {
        if (this.status != ProjectStatus.SUSPENDED) {
            throw new IllegalStateException("只有已暂停的项目才能恢复");
        }
        this.status = ProjectStatus.IN_PROGRESS;
    }

    public void close() {
        if (this.status == ProjectStatus.CLOSED) {
            throw new IllegalStateException("项目已关闭");
        }
        this.status = ProjectStatus.CLOSED;
    }

    public Task createTask(String name, String assigneeId, int estimatedHours) {
        if (this.status == ProjectStatus.CLOSED) {
            throw new IllegalStateException("已关闭的项目不能创建任务");
        }
        Task task = new Task(TaskId.generate(), name, assigneeId, estimatedHours);
        this.tasks.add(task);
        return task;
    }

    public Milestone addMilestone(String name, LocalDate plannedDate) {
        Milestone milestone = new Milestone(MilestoneId.generate(), name, plannedDate);
        this.milestones.add(milestone);
        return milestone;
    }

    public Task getTask(TaskId taskId) {
        return this.tasks.stream()
            .filter(t -> t.id().equals(taskId))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Task not found: " + taskId));
    }

    // Getters
    public ProjectId id() { return id; }
    public String name() { return name; }
    public String customerId() { return customerId; }
    public String contractId() { return contractId; }
    public String pmId() { return pmId; }
    public ProjectStatus status() { return status; }
    public BigDecimal budget() { return budget; }
    public LocalDate startDate() { return startDate; }
    public LocalDate endDate() { return endDate; }
    public List<Task> tasks() { return Collections.unmodifiableList(tasks); }
    public List<Milestone> milestones() { return Collections.unmodifiableList(milestones); }
    public int version() { return version; }

    // Setters for persistence
    public void setName(String name) { this.name = name; }
    public void setBudget(BigDecimal budget) { this.budget = budget; }
    public void setVersion(int version) { this.version = version; }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private ProjectId id = ProjectId.generate();
        private String name;
        private String customerId;
        private String contractId;
        private String pmId;
        private BigDecimal budget;
        private LocalDate startDate;
        private LocalDate endDate;

        public Builder id(ProjectId id) { this.id = id; return this; }
        public Builder name(String name) { this.name = name; return this; }
        public Builder customerId(String customerId) { this.customerId = customerId; return this; }
        public Builder contractId(String contractId) { this.contractId = contractId; return this; }
        public Builder pmId(String pmId) { this.pmId = pmId; return this; }
        public Builder budget(BigDecimal budget) { this.budget = budget; return this; }
        public Builder startDate(LocalDate startDate) { this.startDate = startDate; return this; }
        public Builder endDate(LocalDate endDate) { this.endDate = endDate; return this; }

        public Project build() {
            if (name == null || name.isBlank()) {
                throw new IllegalArgumentException("Project name cannot be blank");
            }
            if (startDate != null && endDate != null && endDate.isBefore(startDate)) {
                throw new IllegalArgumentException("End date cannot be before start date");
            }
            if (budget != null && budget.compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalArgumentException("Budget cannot be negative");
            }
            return new Project(this);
        }
    }
}
