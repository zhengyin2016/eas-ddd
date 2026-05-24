package com.eas.hr.domain.employee;

import com.eas.common.ddd.Identity;

/**
 * 部门ID值对象
 * <p>
 * 注意：这是对Org上下文Department的引用，HR上下文只保存ID。
 * </p>
 */
public record DepartmentId(String value) implements Identity<String> {

    public DepartmentId {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Department ID cannot be null or blank");
        }
    }

    public static DepartmentId of(String value) {
        return new DepartmentId(value);
    }
}
