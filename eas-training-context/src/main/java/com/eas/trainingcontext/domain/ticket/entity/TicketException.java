package com.eas.trainingcontext.domain.ticket.entity;

import com.eas.dddcore.DomainException;

public class TicketException extends DomainException {
    public TicketException(String message) {
        super(message);
    }
}
