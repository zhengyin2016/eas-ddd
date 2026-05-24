package com.eas.trainingcontext.southbound.port.repository;

import com.eas.trainingcontext.domain.tickethistory.entity.TicketHistory;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface TicketHistoryMapperPort {
    void save(TicketHistory ticketHistory);
    List<TicketHistory> findByTicketId(String ticketId);
}
