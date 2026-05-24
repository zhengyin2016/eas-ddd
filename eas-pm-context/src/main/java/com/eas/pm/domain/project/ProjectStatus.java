package com.eas.pm.domain.project;

/**
 * 项目状态值对象
 */
public enum ProjectStatus {
    /**
     * 准备中 - 立项申请刚创建
     */
    PREPARING,
    /**
     * 已审批 - 立项审批通过
     */
    APPROVED,
    /**
     * 进行中 - 项目正式启动
     */
    IN_PROGRESS,
    /**
     * 已暂停 - 项目临时暂停
     */
    SUSPENDED,
    /**
     * 已关闭 - 项目结项
     */
    CLOSED
}
