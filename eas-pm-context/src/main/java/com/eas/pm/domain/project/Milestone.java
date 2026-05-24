package com.eas.pm.domain.project;

import java.time.LocalDate;

/**
 * 里程碑实体（属于Project聚合）
 */
public class Milestone {

    private final MilestoneId id;
    private final String name;
    private final LocalDate plannedDate;
    private LocalDate actualDate;
    private boolean completed;

    public Milestone(MilestoneId id, String name, LocalDate plannedDate) {
        this.id = id;
        this.name = name;
        this.plannedDate = plannedDate;
        this.actualDate = null;
        this.completed = false;
    }

    public void complete(LocalDate actualDate) {
        if (this.completed) {
            throw new IllegalStateException("里程碑已完成");
        }
        this.actualDate = actualDate;
        this.completed = true;
    }

    // Getters
    public MilestoneId id() { return id; }
    public String name() { return name; }
    public LocalDate plannedDate() { return plannedDate; }
    public LocalDate actualDate() { return actualDate; }
    public boolean completed() { return completed; }
}
