package com.eas.hr.domain.recruitment;

import com.eas.common.ddd.Identity;

/**
 * 招聘需求ID值对象
 */
public record RecruitmentId(String value) implements Identity<String> {

    public RecruitmentId {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Recruitment ID cannot be null or blank");
        }
    }

    public static RecruitmentId generate() {
        return new RecruitmentId(java.util.UUID.randomUUID().toString().replace("-", ""));
    }

    public static RecruitmentId of(String value) {
        return new RecruitmentId(value);
    }
}
