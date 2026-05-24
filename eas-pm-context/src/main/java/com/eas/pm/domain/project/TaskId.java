package com.eas.pm.domain.project;

/**
 * 任务ID值对象
 */
public record TaskId(String value) {
    public TaskId {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Task ID cannot be null or blank");
        }
    }

    public static TaskId of(String value) {
        return new TaskId(value);
    }

    public static TaskId generate() {
        return new TaskId(java.util.UUID.randomUUID().toString());
    }
}
