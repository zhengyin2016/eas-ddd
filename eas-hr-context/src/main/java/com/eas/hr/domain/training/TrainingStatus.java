package com.eas.hr.domain.training;

/**
 * 培训计划状态枚举
 */
public enum TrainingStatus {

    /**
     * 草稿 - 未发布
     */
    DRAFT,

    /**
     * 已发布 - 可以报名
     */
    PUBLISHED,

    /**
     * 进行中 - 培训进行中
     */
    IN_PROGRESS,

    /**
     * 已完成 - 培训完成
     */
    COMPLETED,

    /**
     * 已取消 - 培训取消
     */
    CANCELLED
}
