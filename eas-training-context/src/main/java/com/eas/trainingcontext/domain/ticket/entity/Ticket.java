package com.eas.trainingcontext.domain.ticket.entity;

import com.eas.dddcore.AggregateRoot;
import com.eas.trainingcontext.domain.ticket.valueobject.Nominator;
import com.eas.trainingcontext.domain.ticket.valueobject.Nominee;
import com.eas.trainingcontext.domain.ticket.valueobject.TicketStatus;
import com.eas.trainingcontext.domain.tickethistory.entity.TicketHistory;
import com.eas.trainingcontext.domain.tickethistory.valueobject.StateTransit;

public class Ticket extends AggregateRoot<String> {

    private String trainingId;
    private TicketStatus status;

    public Ticket() {
    }

    public Ticket(String id, String trainingId) {
        this.id = id;
        this.trainingId = trainingId;
        this.status = TicketStatus.Available;
    }

    public TicketHistory nominate(Nominee nominee, Nominator nominator) {
        if (status != TicketStatus.Available) {
            throw new TicketException("票的状态不是Available，无法提名");
        }
        TicketHistory history = TicketHistory.nominate(
                this.id,
                new StateTransit(status, TicketStatus.WaitForConfirm),
                nominee.toTicketOwner(),
                nominator.toOperator()
        );
        this.status = TicketStatus.WaitForConfirm;
        return history;
    }

    public String getTrainingId() {
        return trainingId;
    }

    public TicketStatus getStatus() {
        return status;
    }
}
