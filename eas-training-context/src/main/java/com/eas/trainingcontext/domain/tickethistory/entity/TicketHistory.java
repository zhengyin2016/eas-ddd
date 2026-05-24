package com.eas.trainingcontext.domain.tickethistory.entity;

import com.eas.dddcore.Entity;
import com.eas.trainingcontext.domain.tickethistory.valueobject.*;

public class TicketHistory extends Entity<String> {

    private String ticketId;
    private OperationType operationType;
    private StateTransit stateTransit;
    private TicketOwner ticketOwner;
    private Operator operator;
    private java.time.LocalDateTime operatedAt;

    public TicketHistory() {
    }

    public static TicketHistory nominate(String ticketId, StateTransit stateTransit,
                                          TicketOwner ticketOwner, Operator operator) {
        TicketHistory history = new TicketHistory();
        history.id = java.util.UUID.randomUUID().toString();
        history.ticketId = ticketId;
        history.operationType = OperationType.Nominate;
        history.stateTransit = stateTransit;
        history.ticketOwner = ticketOwner;
        history.operator = operator;
        history.operatedAt = java.time.LocalDateTime.now();
        return history;
    }

    public String getTicketId() { return ticketId; }
    public OperationType getOperationType() { return operationType; }
    public StateTransit getStateTransit() { return stateTransit; }
    public TicketOwner getTicketOwner() { return ticketOwner; }
    public Operator getOperator() { return operator; }
    public java.time.LocalDateTime getOperatedAt() { return operatedAt; }
}
