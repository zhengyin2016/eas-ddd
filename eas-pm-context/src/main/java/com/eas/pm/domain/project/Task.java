package com.eas.pm.domain.project;

/**
 * 任务实体（属于Project聚合）
 */
public class Task {

    private final TaskId id;
    private final String name;
    private final String projectId;
    private String assigneeId;
    private TaskStatus status;
    private int priority;
    private int estimatedHours;
    private int actualHours;

    public Task(TaskId id, String name, String assigneeId, int estimatedHours) {
        this.id = id;
        this.name = name;
        this.projectId = null; // Will be set by parent Project
        this.assigneeId = assigneeId;
        this.status = TaskStatus.TODO;
        this.priority = 0;
        this.estimatedHours = estimatedHours;
        this.actualHours = 0;
    }

    public void start() {
        if (this.status != TaskStatus.TODO) {
            throw new IllegalStateException("只有待办任务才能开始");
        }
        this.status = TaskStatus.IN_PROGRESS;
    }

    public void complete(int actualHours) {
        if (this.status != TaskStatus.IN_PROGRESS) {
            throw new IllegalStateException("只有进行中的任务才能完成");
        }
        if (actualHours < 0) {
            throw new IllegalArgumentException("实际工时不能为负数");
        }
        this.actualHours = actualHours;
        this.status = TaskStatus.DONE;
    }

    // Getters
    public TaskId id() { return id; }
    public String name() { return name; }
    public String projectId() { return projectId; }
    public String assigneeId() { return assigneeId; }
    public TaskStatus status() { return status; }
    public int priority() { return priority; }
    public int estimatedHours() { return estimatedHours; }
    public int actualHours() { return actualHours; }

    // Setters
    public void setAssigneeId(String assigneeId) { this.assigneeId = assigneeId; }
    public void setProjectId(String projectId) { this.projectId = projectId; }
    public void setPriority(int priority) { this.priority = priority; }
}
