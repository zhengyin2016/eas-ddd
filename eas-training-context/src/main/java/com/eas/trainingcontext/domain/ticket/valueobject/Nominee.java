package com.eas.trainingcontext.domain.ticket.valueobject;

import com.eas.dddcore.ValueObject;
import com.eas.trainingcontext.domain.tickethistory.valueobject.TicketOwner;

public record Nominee(String employeeId, String name) implements ValueObject {
    public TicketOwner toTicketOwner() {
        return new TicketOwner(employeeId, name);
    }
}
