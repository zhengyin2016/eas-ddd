package com.eas.pm.message;

/**
 * 创建任务请求DTO
 */
public record CreateTaskRequest(
    String projectId,
    String name,
    String assigneeId,
    int estimatedHours
) {}
