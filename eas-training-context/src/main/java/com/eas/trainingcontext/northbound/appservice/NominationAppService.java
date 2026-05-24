package com.eas.trainingcontext.northbound.appservice;

import com.eas.dddcore.ApplicationException;
import com.eas.trainingcontext.domain.ticket.entity.TicketException;
import com.eas.trainingcontext.domain.ticket.service.TicketService;
import com.eas.trainingcontext.message.NominationRequest;
import com.eas.trainingcontext.message.NominationResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 提名应用服务（书中20.3.5节）
 *
 * Spring作为依赖注入框架，声明式事务。
 * 异常分层：DomainException → ApplicationException
 * 消息契约对象支持序列化（JavaBean规范）
 */
@Service
@Transactional
public class NominationAppService {

    private final TicketService ticketService;

    public NominationAppService(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    public NominationResponse nominate(NominationRequest request) {
        try {
            ticketService.nominate(
                    request.getTrainingId(),
                    request.getNomineeEmployeeId(),
                    request.getNomineeName(),
                    request.getNominatorEmployeeId(),
                    request.getNominatorName()
            );
            return new NominationResponse(true, "提名成功");
        } catch (TicketException e) {
            throw new ApplicationException(e.getMessage(), e);
        }
    }
}
