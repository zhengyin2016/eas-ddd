package com.eas.pm.domain.project;

import java.util.Objects;

/**
 * 项目ID值对象
 */
public record ProjectId(String value) {
    public ProjectId {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Project ID cannot be null or blank");
        }
    }

    public static ProjectId of(String value) {
        return new ProjectId(value);
    }

    public static ProjectId generate() {
        return new ProjectId(java.util.UUID.randomUUID().toString());
    }
}
