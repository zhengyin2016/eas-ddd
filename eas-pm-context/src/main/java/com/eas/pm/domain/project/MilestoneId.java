package com.eas.pm.domain.project;

/**
 * 里程碑ID值对象
 */
public record MilestoneId(String value) {
    public MilestoneId {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Milestone ID cannot be null or blank");
        }
    }

    public static MilestoneId of(String value) {
        return new MilestoneId(value);
    }

    public static MilestoneId generate() {
        return new MilestoneId(java.util.UUID.randomUUID().toString());
    }
}
