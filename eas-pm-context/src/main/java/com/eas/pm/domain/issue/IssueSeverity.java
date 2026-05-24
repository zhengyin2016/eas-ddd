package com.eas.pm.domain.issue;

/**
 * 问题严重程度值对象
 */
public enum IssueSeverity {
    /**
     * 严重 - 阻塞项目进展
     */
    CRITICAL,
    /**
     * 重要 - 影响项目进展
     */
    MAJOR,
    /**
     * 一般 - 不影响项目进展
     */
    MINOR
}
