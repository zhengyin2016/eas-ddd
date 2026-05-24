package com.eas.trainingcontext.southbound.adapter.repository;

import com.eas.trainingcontext.domain.tickethistory.entity.TicketHistory;
import com.eas.trainingcontext.domain.tickethistory.repository.TicketHistoryRepository;
import com.eas.trainingcontext.southbound.port.repository.TicketHistoryMapperPort;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class TicketHistoryRepositoryAdapter implements TicketHistoryRepository {

    private final TicketHistoryMapperPort mapperPort;

    public TicketHistoryRepositoryAdapter(TicketHistoryMapperPort mapperPort) {
        this.mapperPort = mapperPort;
    }

    @Override
    public void save(TicketHistory ticketHistory) {
        mapperPort.save(ticketHistory);
    }

    @Override
    public List<TicketHistory> findByTicketId(String ticketId) {
        return mapperPort.findByTicketId(ticketId);
    }
}
