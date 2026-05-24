package com.eas.trainingcontext.domain.tickethistory.valueobject;

import com.eas.dddcore.ValueObject;
import com.eas.trainingcontext.domain.ticket.valueobject.TicketStatus;

public record StateTransit(TicketStatus fromStatus, TicketStatus toStatus) implements ValueObject {
}
