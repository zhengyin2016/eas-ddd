package com.eas.pm.message;

import com.eas.pm.domain.issue.IssueSeverity;

/**
 * 创建问题请求DTO
 */
public record CreateIssueRequest(
    String projectId,
    String title,
    String description,
    IssueSeverity severity,
    int priority
) {}
