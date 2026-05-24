package com.eas.pm.domain.assignment;

/**
 * 人员分配ID值对象
 */
public record AssignmentId(String value) {
    public AssignmentId {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Assignment ID cannot be null or blank");
        }
    }

    public static AssignmentId of(String value) {
        return new AssignmentId(value);
    }

    public static AssignmentId generate() {
        return new AssignmentId(java.util.UUID.randomUUID().toString());
    }
}
