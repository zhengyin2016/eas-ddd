package com.eas.hr.domain.attendance;

import com.eas.common.ddd.Identity;

/**
 * 考勤ID值对象
 */
public record AttendanceId(String value) implements Identity<String> {

    public AttendanceId {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Attendance ID cannot be null or blank");
        }
    }

    public static AttendanceId generate() {
        return new AttendanceId(java.util.UUID.randomUUID().toString().replace("-", ""));
    }

    public static AttendanceId of(String value) {
        return new AttendanceId(value);
    }
}
