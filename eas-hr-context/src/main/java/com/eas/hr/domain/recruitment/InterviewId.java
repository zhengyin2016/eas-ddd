package com.eas.hr.domain.recruitment;

import com.eas.common.ddd.Identity;

/**
 * 面试ID值对象
 */
public record InterviewId(String value) implements Identity<String> {

    public InterviewId {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Interview ID cannot be null or blank");
        }
    }

    public static InterviewId generate() {
        return new InterviewId(java.util.UUID.randomUUID().toString().replace("-", ""));
    }

    public static InterviewId of(String value) {
        return new InterviewId(value);
    }
}
