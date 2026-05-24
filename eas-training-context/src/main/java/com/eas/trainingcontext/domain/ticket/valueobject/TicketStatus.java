package com.eas.trainingcontext.domain.ticket.valueobject;

import com.eas.dddcore.ValueObject;

public enum TicketStatus implements ValueObject {
    Available,
    WaitForConfirm,
    Confirmed,
    Declined,
    Cancelled
}
