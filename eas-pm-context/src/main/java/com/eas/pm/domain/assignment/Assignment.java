package com.eas.pm.domain.assignment;

import java.time.LocalDate;

/**
 * 人员分配聚合根
 * 连接PM上下文和HR上下文的核心聚合
 */
public class Assignment {

    private final AssignmentId id;
    private final String projectId;
    private final String employeeId;
    private final AssignmentRole role;
    private int allocation;
    private final LocalDate startDate;
    private final LocalDate endDate;
    private boolean released;
    private LocalDate releasedAt;
    private int version;

    public Assignment(AssignmentId id, String projectId, String employeeId,
                      AssignmentRole role, int allocation, LocalDate startDate, LocalDate endDate) {
        this.id = id;
        this.projectId = projectId;
        this.employeeId = employeeId;
        this.role = role;
        this.setAllocation(allocation);
        this.startDate = startDate;
        this.endDate = endDate;
        this.released = false;
        this.releasedAt = null;
        this.version = 0;
    }

    public void updateAllocation(int newAllocation) {
        if (this.released) {
            throw new IllegalStateException("已释放的分配不能修改");
        }
        setAllocation(newAllocation);
    }

    public void release() {
        if (this.released) {
            throw new IllegalStateException("分配已释放");
        }
        this.released = true;
        this.releasedAt = LocalDate.now();
    }

    private void setAllocation(int allocation) {
        if (allocation < 0 || allocation > 100) {
            throw new IllegalArgumentException("分配比例必须在0-100之间");
        }
        this.allocation = allocation;
    }

    // Getters
    public AssignmentId id() { return id; }
    public String projectId() { return projectId; }
    public String employeeId() { return employeeId; }
    public AssignmentRole role() { return role; }
    public int allocation() { return allocation; }
    public LocalDate startDate() { return startDate; }
    public LocalDate endDate() { return endDate; }
    public boolean released() { return released; }
    public LocalDate releasedAt() { return releasedAt; }
    public int version() { return version; }

    public void setVersion(int version) { this.version = version; }
}
