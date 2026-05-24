package com.eas.hr.domain.recruitment;

/**
 * 招聘需求状态枚举
 */
public enum RecruitmentStatus {

    /**
     * 草稿 - 未提交
     */
    DRAFT,

    /**
     * 待审批 - 已提交等待审批
     */
    PENDING,

    /**
     * 已通过 - 审批通过
     */
    APPROVED,

    /**
     * 已拒绝 - 审批拒绝
     */
    REJECTED,

    /**
     * 已取消 - 取消招聘
     */
    CANCELLED,

    /**
     * 已完成 - 招聘完成
     */
    FULFILLED
}
