package com.eas.hr.domain.event;

import com.eas.common.ddd.DomainEvent;

/**
 * 员工离职领域事件
 */
public class EmployeeResignedEvent extends DomainEvent {

    private final String employeeName;
    private final String previousStatus;
    private final String reason;

    public EmployeeResignedEvent(String aggregateId, String employeeName, String previousStatus, String reason) {
        super(aggregateId, "Employee");
        this.employeeName = employeeName;
        this.previousStatus = previousStatus;
        this.reason = reason;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public String getPreviousStatus() {
        return previousStatus;
    }

    public String getReason() {
        return reason;
    }
}
