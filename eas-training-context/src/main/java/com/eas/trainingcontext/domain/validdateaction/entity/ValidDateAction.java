package com.eas.trainingcontext.domain.validdateaction.entity;

import com.eas.dddcore.Entity;

public class ValidDateAction extends Entity<String> {

    private String validDateId;
    private String action;

    public ValidDateAction() {
    }

    public ValidDateAction(String id, String validDateId, String action) {
        this.id = id;
        this.validDateId = validDateId;
        this.action = action;
    }

    public String getValidDateId() { return validDateId; }
    public String getAction() { return action; }
}
