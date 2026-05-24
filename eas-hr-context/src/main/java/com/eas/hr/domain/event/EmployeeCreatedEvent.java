package com.eas.hr.domain.event;

import com.eas.common.ddd.DomainEvent;

/**
 * 员工创建领域事件
 */
public class EmployeeCreatedEvent extends DomainEvent {

    private final String employeeName;

    public EmployeeCreatedEvent(String aggregateId, String employeeName) {
        super(aggregateId, "Employee");
        this.employeeName = employeeName;
    }

    public String getEmployeeName() {
        return employeeName;
    }
}
