package com.eas.pm.domain.iteration;

/**
 * 迭代ID值对象
 */
public record IterationId(String value) {
    public IterationId {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Iteration ID cannot be null or blank");
        }
    }

    public static IterationId of(String value) {
        return new IterationId(value);
    }

    public static IterationId generate() {
        return new IterationId(java.util.UUID.randomUUID().toString());
    }
}
