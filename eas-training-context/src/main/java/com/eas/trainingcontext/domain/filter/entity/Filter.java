package com.eas.trainingcontext.domain.filter.entity;

import com.eas.dddcore.Entity;

public class Filter extends Entity<String> {

    private String trainingId;
    private String type;
    private String rule;

    public Filter() {
    }

    public Filter(String id, String trainingId, String type, String rule) {
        this.id = id;
        this.trainingId = trainingId;
        this.type = type;
        this.rule = rule;
    }

    public String getTrainingId() { return trainingId; }
    public String getType() { return type; }
    public String getRule() { return rule; }
}
