package com.eas.trainingcontext.domain.cancellingaction.entity;

import com.eas.dddcore.Entity;

public class CancellingAction extends Entity<String> {

    private String ticketId;
    private String reason;
    private String operatorId;

    public CancellingAction() {
    }

    public CancellingAction(String id, String ticketId, String reason, String operatorId) {
        this.id = id;
        this.ticketId = ticketId;
        this.reason = reason;
        this.operatorId = operatorId;
    }

    public String getTicketId() { return ticketId; }
    public String getReason() { return reason; }
    public String getOperatorId() { return operatorId; }
}
