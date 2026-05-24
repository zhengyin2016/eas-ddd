package com.eas.trainingcontext.southbound.adapter.repository;

import com.eas.trainingcontext.domain.ticket.entity.Ticket;
import com.eas.trainingcontext.domain.ticket.repository.TicketRepository;
import com.eas.trainingcontext.domain.ticket.valueobject.TicketStatus;
import com.eas.trainingcontext.southbound.port.repository.TicketMapperPort;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class TicketRepositoryAdapter implements TicketRepository {

    private final TicketMapperPort mapperPort;

    public TicketRepositoryAdapter(TicketMapperPort mapperPort) {
        this.mapperPort = mapperPort;
    }

    @Override
    public Optional<Ticket> findById(String id) {
        return mapperPort.findById(id);
    }

    @Override
    public Optional<Ticket> findByTrainingIdAndStatusAvailable(String trainingId) {
        return mapperPort.findByTrainingIdAndStatus(trainingId, TicketStatus.Available.name());
    }

    @Override
    public void save(Ticket ticket) {
        mapperPort.save(ticket);
    }
}
