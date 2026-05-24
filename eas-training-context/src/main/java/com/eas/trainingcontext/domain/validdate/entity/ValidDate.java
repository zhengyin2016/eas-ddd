package com.eas.trainingcontext.domain.validdate.entity;

import com.eas.dddcore.Entity;

public class ValidDate extends Entity<String> {

    private String trainingId;
    private String formula;
    private java.time.LocalDateTime date;
    private java.time.LocalDateTime time;

    public ValidDate() {
    }

    public ValidDate(String id, String trainingId, String formula) {
        this.id = id;
        this.trainingId = trainingId;
        this.formula = formula;
    }

    public String getTrainingId() { return trainingId; }
    public String getFormula() { return formula; }
    public java.time.LocalDateTime getDate() { return date; }
    public java.time.LocalDateTime getTime() { return time; }
}
