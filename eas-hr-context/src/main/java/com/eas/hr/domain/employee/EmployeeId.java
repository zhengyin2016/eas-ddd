package com.eas.hr.domain.employee;

import com.eas.common.ddd.Identity;

/**
 * 员工ID值对象
 */
public record EmployeeId(String value) implements Identity<String> {

    public EmployeeId {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Employee ID cannot be null or blank");
        }
    }

    /**
     * 生成新的员工ID
     *
     * @return 新的员工ID
     */
    public static EmployeeId generate() {
        return new EmployeeId(java.util.UUID.randomUUID().toString().replace("-", ""));
    }

    /**
     * 从字符串创建员工ID
     *
     * @param value ID字符串
     * @return 员工ID
     */
    public static EmployeeId of(String value) {
        return new EmployeeId(value);
    }
}
