package com.eas.hr.domain.event;

import com.eas.common.ddd.DomainEvent;

/**
 * 员工调岗领域事件
 */
public class EmployeeTransferredEvent extends DomainEvent {

    private final String fromDepartmentId;
    private final String fromPositionId;
    private final String toDepartmentId;
    private final String toPositionId;

    public EmployeeTransferredEvent(String aggregateId, String fromDepartmentId, String fromPositionId,
                                    String toDepartmentId, String toPositionId) {
        super(aggregateId, "Employee");
        this.fromDepartmentId = fromDepartmentId;
        this.fromPositionId = fromPositionId;
        this.toDepartmentId = toDepartmentId;
        this.toPositionId = toPositionId;
    }

    public String getFromDepartmentId() {
        return fromDepartmentId;
    }

    public String getFromPositionId() {
        return fromPositionId;
    }

    public String getToDepartmentId() {
        return toDepartmentId;
    }

    public String getToPositionId() {
        return toPositionId;
    }
}
