package com.eas.trainingcontext.domain.attendance.entity;

import com.eas.dddcore.AggregateRoot;

public class Attendance extends AggregateRoot<String> {

    private String trainingId;
    private String employeeId;
    private boolean present;

    public Attendance() {
    }

    public Attendance(String id, String trainingId, String employeeId) {
        this.id = id;
        this.trainingId = trainingId;
        this.employeeId = employeeId;
        this.present = false;
    }

    public void checkIn() {
        this.present = true;
    }

    public String getTrainingId() { return trainingId; }
    public String getEmployeeId() { return employeeId; }
    public boolean isPresent() { return present; }
}
