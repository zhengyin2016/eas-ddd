package com.eas.trainingcontext.domain.tickethistory.valueobject;

import com.eas.dddcore.ValueObject;

public record TicketOwner(String employeeId, String name) implements ValueObject {
}
