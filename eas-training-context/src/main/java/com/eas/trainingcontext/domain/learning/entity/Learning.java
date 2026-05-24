package com.eas.trainingcontext.domain.learning.entity;

import com.eas.dddcore.Entity;

public class Learning extends Entity<String> {

    private String courseId;
    private String employeeId;

    public Learning() {
    }

    public Learning(String id, String courseId, String employeeId) {
        this.id = id;
        this.courseId = courseId;
        this.employeeId = employeeId;
    }

    public boolean isLearnedBy(String employeeId) {
        return this.employeeId.equals(employeeId);
    }

    public String getCourseId() { return courseId; }
    public String getEmployeeId() { return employeeId; }
}
