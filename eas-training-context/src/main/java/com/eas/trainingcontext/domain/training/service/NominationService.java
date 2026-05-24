package com.eas.trainingcontext.domain.training.service;

import com.eas.trainingcontext.domain.ticket.service.TicketService;

/**
 * 提名领域服务（书中20.3.4节服务驱动设计发现）
 *
 * 封装提名候选人的跨聚合业务逻辑，由NominationAppService调用。
 * 书中通过序列图发现：NominationAppService需要引入一个领域服务来封装
 * TicketService、LearningRepository等之间的协调职责。
 */
public class NominationService {

    private final TicketService ticketService;

    public NominationService(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    public void nominate(String trainingId, String nomineeEmployeeId, String nomineeName,
                         String nominatorEmployeeId, String nominatorName) {
        ticketService.nominate(trainingId, nomineeEmployeeId, nomineeName,
                nominatorEmployeeId, nominatorName);
    }
}
