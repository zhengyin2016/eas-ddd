package com.eas.pm.domain.iteration;

import java.time.LocalDate;

/**
 * 迭代聚合根
 */
public class Iteration {

    private final IterationId id;
    private final String projectId;
    private final String name;
    private final LocalDate startDate;
    private final LocalDate endDate;
    private IterationStatus status;
    private int version;

    public Iteration(IterationId id, String projectId, String name,
                     LocalDate startDate, LocalDate endDate) {
        if (endDate.isBefore(startDate)) {
            throw new IllegalArgumentException("结束日期不能早于开始日期");
        }
        this.id = id;
        this.projectId = projectId;
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = IterationStatus.PLANNED;
        this.version = 0;
    }

    public void start() {
        if (this.status != IterationStatus.PLANNED) {
            throw new IllegalStateException("只有计划中的迭代才能启动");
        }
        this.status = IterationStatus.ACTIVE;
    }

    public void complete() {
        if (this.status != IterationStatus.ACTIVE) {
            throw new IllegalStateException("只有进行中的迭代才能完成");
        }
        this.status = IterationStatus.COMPLETED;
    }

    // Getters
    public IterationId id() { return id; }
    public String projectId() { return projectId; }
    public String name() { return name; }
    public LocalDate startDate() { return startDate; }
    public LocalDate endDate() { return endDate; }
    public IterationStatus status() { return status; }
    public int version() { return version; }

    public void setVersion(int version) { this.version = version; }
}
