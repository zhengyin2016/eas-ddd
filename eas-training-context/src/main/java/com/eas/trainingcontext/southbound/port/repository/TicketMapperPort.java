package com.eas.trainingcontext.southbound.port.repository;

import com.eas.trainingcontext.domain.ticket.entity.Ticket;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Optional;

@Mapper
public interface TicketMapperPort {
    Optional<Ticket> findById(@Param("id") String id);
    Optional<Ticket> findByTrainingIdAndStatus(@Param("trainingId") String trainingId,
                                                @Param("status") String status);
    void save(Ticket ticket);
}
