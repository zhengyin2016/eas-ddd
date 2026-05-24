package com.eas.trainingcontext.domain.ticket.valueobject;

import com.eas.dddcore.ValueObject;
import com.eas.trainingcontext.domain.tickethistory.valueobject.Operator;

public record Nominator(String employeeId, String name) implements ValueObject {
    public Operator toOperator() {
        return new Operator(employeeId, name);
    }
}
