package com.eas.hr.domain.employee;

/**
 * 员工状态枚举
 */
public enum EmployeeStatus {

    /**
     * 待入职 - 已录用但未正式入职
     */
    PENDING,

    /**
     * 试用期 - 试用期内
     */
    PROBATION,

    /**
     * 正式 - 已转正
     */
    REGULAR,

    /**
     * 调岗中 - 正在办理调岗手续
     */
    TRANSFERRING,

    /**
     * 离职中 - 正在办理离职手续
     */
    RESIGNING,

    /**
     * 已离职 - 已离职
     */
    RESIGNED
}
