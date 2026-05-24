package com.eas.pm.domain.issue;

/**
 * 问题ID值对象
 */
public record IssueId(String value) {
    public IssueId {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Issue ID cannot be null or blank");
        }
    }

    public static IssueId of(String value) {
        return new IssueId(value);
    }

    public static IssueId generate() {
        return new IssueId(java.util.UUID.randomUUID().toString());
    }
}
