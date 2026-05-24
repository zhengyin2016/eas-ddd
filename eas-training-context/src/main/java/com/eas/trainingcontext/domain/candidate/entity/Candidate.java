package com.eas.trainingcontext.domain.candidate.entity;

import com.eas.dddcore.AggregateRoot;

public class Candidate extends AggregateRoot<String> {

    private String trainingId;
    private String employeeId;

    public Candidate() {
    }

    public Candidate(String id, String trainingId, String employeeId) {
        this.id = id;
        this.trainingId = trainingId;
        this.employeeId = employeeId;
    }

    public String getTrainingId() { return trainingId; }
    public String getEmployeeId() { return employeeId; }
}
