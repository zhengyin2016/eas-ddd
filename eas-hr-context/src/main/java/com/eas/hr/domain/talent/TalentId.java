package com.eas.hr.domain.talent;

import com.eas.common.ddd.Identity;

/**
 * 储备人才ID值对象
 */
public record TalentId(String value) implements Identity<String> {

    public TalentId {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Talent ID cannot be null or blank");
        }
    }

    public static TalentId generate() {
        return new TalentId(java.util.UUID.randomUUID().toString().replace("-", ""));
    }

    public static TalentId of(String value) {
        return new TalentId(value);
    }
}
