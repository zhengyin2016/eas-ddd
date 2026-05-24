package com.eas.hr.domain.training;

import com.eas.common.ddd.Identity;

/**
 * 培训计划ID值对象
 */
public record TrainingId(String value) implements Identity<String> {

    public TrainingId {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Training ID cannot be null or blank");
        }
    }

    public static TrainingId generate() {
        return new TrainingId(java.util.UUID.randomUUID().toString().replace("-", ""));
    }

    public static TrainingId of(String value) {
        return new TrainingId(value);
    }
}
