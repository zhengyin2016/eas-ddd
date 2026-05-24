package com.eas.trainingcontext.domain.ticket.repository;

import com.eas.trainingcontext.domain.ticket.entity.Ticket;

import java.util.Optional;

public interface TicketRepository {
    Optional<Ticket> findById(String id);
    Optional<Ticket> findByTrainingIdAndStatusAvailable(String trainingId);
    void save(Ticket ticket);
}
