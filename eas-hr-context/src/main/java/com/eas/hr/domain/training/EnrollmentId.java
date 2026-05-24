package com.eas.hr.domain.training;

import com.eas.common.ddd.Identity;

/**
 * 报名ID值对象
 */
public record EnrollmentId(String value) implements Identity<String> {

    public EnrollmentId {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Enrollment ID cannot be null or blank");
        }
    }

    public static EnrollmentId generate() {
        return new EnrollmentId(java.util.UUID.randomUUID().toString().replace("-", ""));
    }

    public static EnrollmentId of(String value) {
        return new EnrollmentId(value);
    }
}
