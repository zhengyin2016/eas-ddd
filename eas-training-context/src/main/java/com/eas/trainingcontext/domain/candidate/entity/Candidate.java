package com.eas.trainingcontext.domain.candidate.entity;

import com.eas.dddcore.AggregateRoot;

public class Candidate extends AggregateRoot<String> {

    private String trainingId;
    private String employeeId;
    private String employeeName;

    public Candidate() {
    }

    public Candidate(String id, String trainingId, String employeeId) {
        this(id, trainingId, employeeId, null);
    }

    public Candidate(String id, String trainingId, String employeeId, String employeeName) {
        this.id = id;
        this.trainingId = trainingId;
        this.employeeId = employeeId;
        this.employeeName = employeeName;
    }

    public String getTrainingId() { return trainingId; }
    public String getEmployeeId() { return employeeId; }
    public String getEmployeeName() { return employeeName; }
}
