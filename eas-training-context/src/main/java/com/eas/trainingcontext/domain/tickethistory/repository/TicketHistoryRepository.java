package com.eas.trainingcontext.domain.tickethistory.repository;

import com.eas.trainingcontext.domain.tickethistory.entity.TicketHistory;

import java.util.List;

public interface TicketHistoryRepository {
    void save(TicketHistory ticketHistory);
    List<TicketHistory> findByTicketId(String ticketId);
}
