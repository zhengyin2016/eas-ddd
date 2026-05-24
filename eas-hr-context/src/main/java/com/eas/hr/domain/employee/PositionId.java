package com.eas.hr.domain.employee;

import com.eas.common.ddd.Identity;

/**
 * 岗位ID值对象
 * <p>
 * 注意：这是对Org上下文Position的引用，HR上下文只保存ID。
 * </p>
 */
public record PositionId(String value) implements Identity<String> {

    public PositionId {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Position ID cannot be null or blank");
        }
    }

    public static PositionId of(String value) {
        return new PositionId(value);
    }
}
