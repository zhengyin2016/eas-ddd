package com.eas.hr.domain.training;

/**
 * 报名状态枚举
 */
public enum EnrollmentStatus {

    /**
     * 待确认 - 已报名等待确认
     */
    PENDING,

    /**
     * 已确认 - 报名已确认
     */
    CONFIRMED,

    /**
     * 已签到 - 已签到参加培训
     */
    ATTENDED,

    /**
     * 已完成 - 已完成培训
     */
    COMPLETED,

    /**
     * 已取消 - 取消报名
     */
    CANCELLED
}
