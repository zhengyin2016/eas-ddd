package com.eas.pm.domain.issue;

/**
 * 问题状态值对象
 */
public enum IssueStatus {
    /**
     * 待处理
     */
    OPEN,
    /**
     * 处理中
     */
    IN_PROGRESS,
    /**
     * 已解决
     */
    RESOLVED,
    /**
     * 已关闭
     */
    CLOSED
}
