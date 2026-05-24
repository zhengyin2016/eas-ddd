package com.eas.hr.domain.talent;

/**
 * 储备人才状态枚举
 */
public enum TalentStatus {

    /**
     * 新建 - 刚录入系统
     */
    NEW,

    /**
     * 联系中 - 正在联系
     */
    CONTACTING,

    /**
     * 已面试 - 完成面试
     */
    INTERVIEWED,

    /**
     * 已通过 - 面试通过
     */
    APPROVED,

    /**
     * 已转化 - 已转为员工
     */
    CONVERTED,

    /**
     * 已拒绝 - 未通过
     */
    REJECTED
}
