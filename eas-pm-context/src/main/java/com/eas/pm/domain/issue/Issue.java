package com.eas.pm.domain.issue;

import java.time.LocalDateTime;

/**
 * 问题聚合根
 */
public class Issue {

    private final IssueId id;
    private final String projectId;
    private final String title;
    private final String description;
    private final IssueSeverity severity;
    private final int priority;
    private String assigneeId;
    private IssueStatus status;
    private final LocalDateTime createdAt;
    private LocalDateTime resolvedAt;
    private String resolution;
    private int version;

    public Issue(IssueId id, String projectId, String title, String description,
                 IssueSeverity severity, int priority) {
        this.id = id;
        this.projectId = projectId;
        this.title = title;
        this.description = description;
        this.severity = severity;
        this.priority = priority;
        this.status = IssueStatus.OPEN;
        this.createdAt = LocalDateTime.now();
        this.resolvedAt = null;
        this.resolution = null;
        this.version = 0;
    }

    public void assign(String assigneeId) {
        if (this.status == IssueStatus.CLOSED) {
            throw new IllegalStateException("已关闭的问题不能分配");
        }
        this.assigneeId = assigneeId;
        if (this.status == IssueStatus.OPEN) {
            this.status = IssueStatus.IN_PROGRESS;
        }
    }

    public void resolve(String resolution) {
        if (this.status != IssueStatus.IN_PROGRESS) {
            throw new IllegalStateException("只有处理中的问题才能解决");
        }
        this.resolution = resolution;
        this.resolvedAt = LocalDateTime.now();
        this.status = IssueStatus.RESOLVED;
    }

    public void close() {
        if (this.status != IssueStatus.RESOLVED) {
            throw new IllegalStateException("只有已解决的问题才能关闭");
        }
        this.status = IssueStatus.CLOSED;
    }

    // Getters
    public IssueId id() { return id; }
    public String projectId() { return projectId; }
    public String title() { return title; }
    public String description() { return description; }
    public IssueSeverity severity() { return severity; }
    public int priority() { return priority; }
    public String assigneeId() { return assigneeId; }
    public IssueStatus status() { return status; }
    public LocalDateTime createdAt() { return createdAt; }
    public LocalDateTime resolvedAt() { return resolvedAt; }
    public String resolution() { return resolution; }
    public int version() { return version; }

    public void setVersion(int version) { this.version = version; }
}
