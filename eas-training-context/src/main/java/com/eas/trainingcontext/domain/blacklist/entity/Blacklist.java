package com.eas.trainingcontext.domain.blacklist.entity;

import com.eas.dddcore.AggregateRoot;

public class Blacklist extends AggregateRoot<String> {

    private String employeeId;
    private String reason;

    public Blacklist() {
    }

    public Blacklist(String id, String employeeId, String reason) {
        this.id = id;
        this.employeeId = employeeId;
        this.reason = reason;
    }

    public String getEmployeeId() { return employeeId; }
    public String getReason() { return reason; }
}
