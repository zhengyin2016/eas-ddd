package com.eas.hr.domain.attendance;

/**
 * 考勤状态枚举
 */
public enum AttendanceStatus {

    /**
     * 正常 - 正常出勤
     */
    NORMAL,

    /**
     * 迟到 - 迟到
     */
    LATE,

    /**
     * 早退 - 早退
     */
    EARLY_LEAVE,

    /**
     * 缺勤 - 缺勤
     */
    ABSENT
}
