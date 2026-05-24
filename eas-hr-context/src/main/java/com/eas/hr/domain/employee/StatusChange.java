package com.eas.hr.domain.employee;

import com.eas.common.ddd.ValueObject;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * 员工状态变更记录值对象
 *
 * @param fromStatus 原状态
 * @param toStatus   新状态
 * @param changeTime 变更时间
 * @param reason     变更原因（可选）
 */
public record StatusChange(EmployeeStatus fromStatus, EmployeeStatus toStatus,
                           LocalDateTime changeTime, String reason) implements ValueObject {

    public StatusChange {
        Objects.requireNonNull(fromStatus, "From status cannot be null");
        Objects.requireNonNull(toStatus, "To status cannot be null");
        Objects.requireNonNull(changeTime, "Change time cannot be null");
    }

    /**
     * 创建无原因的状态变更记录
     *
     * @param fromStatus 原状态
     * @param toStatus   新状态
     * @return 状态变更记录
     */
    public static StatusChange of(EmployeeStatus fromStatus, EmployeeStatus toStatus) {
        return new StatusChange(fromStatus, toStatus, LocalDateTime.now(), null);
    }

    /**
     * 创建有原因的状态变更记录
     *
     * @param fromStatus 原状态
     * @param toStatus   新状态
     * @param reason     变更原因
     * @return 状态变更记录
     */
    public static StatusChange withReason(EmployeeStatus fromStatus, EmployeeStatus toStatus, String reason) {
        return new StatusChange(fromStatus, toStatus, LocalDateTime.now(), reason);
    }

    /**
     * 检查是否有变更原因
     *
     * @return true如果有变更原因
     */
    public boolean hasReason() {
        return reason != null && !reason.isBlank();
    }
}
