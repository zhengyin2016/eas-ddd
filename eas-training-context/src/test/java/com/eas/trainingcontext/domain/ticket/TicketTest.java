package com.eas.trainingcontext.domain.ticket;

import com.eas.trainingcontext.domain.ticket.entity.Ticket;
import com.eas.trainingcontext.domain.ticket.entity.TicketException;
import com.eas.trainingcontext.domain.ticket.valueobject.Nominator;
import com.eas.trainingcontext.domain.ticket.valueobject.Nominee;
import com.eas.trainingcontext.domain.ticket.valueobject.TicketStatus;
import com.eas.trainingcontext.domain.tickethistory.entity.TicketHistory;
import com.eas.trainingcontext.domain.tickethistory.valueobject.OperationType;
import com.eas.trainingcontext.domain.tickethistory.valueobject.Operator;
import com.eas.trainingcontext.domain.tickethistory.valueobject.StateTransit;
import com.eas.trainingcontext.domain.tickethistory.valueobject.TicketOwner;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Ticket聚合的测试驱动开发（书中20.3.4节）
 *
 * 三个测试用例：
 * 1. 验证提名之前的票状态必须为Available
 * 2. 提名给候选人后，票的状态更改为WaitForConfirm
 * 3. 为票生成提名历史记录
 */
public class TicketTest {

    @Test
    public void should_throw_exception_when_nominate_with_non_available_status() {
        Ticket ticket = new Ticket("ticket-1", "training-1");
        Nominee nominee = new Nominee("emp-001", "张三");
        Nominator nominator = new Nominator("emp-002", "李四");

        ticket.nominate(nominee, nominator);

        assertThrows(TicketException.class, () -> {
            ticket.nominate(nominee, nominator);
        });
    }

    @Test
    public void should_change_status_to_wait_for_confirm_after_nominate() {
        Ticket ticket = new Ticket("ticket-1", "training-1");
        Nominee nominee = new Nominee("emp-001", "张三");
        Nominator nominator = new Nominator("emp-002", "李四");

        ticket.nominate(nominee, nominator);

        assertEquals(TicketStatus.WaitForConfirm, ticket.getStatus());
    }

    @Test
    public void should_generate_ticket_history_after_nominate() {
        Ticket ticket = new Ticket("ticket-1", "training-1");
        Nominee nominee = new Nominee("emp-001", "张三");
        Nominator nominator = new Nominator("emp-002", "李四");

        TicketHistory history = ticket.nominate(nominee, nominator);

        assertNotNull(history);
        assertEquals("ticket-1", history.getTicketId());
        assertEquals(OperationType.Nominate, history.getOperationType());
        assertEquals(new StateTransit(TicketStatus.Available, TicketStatus.WaitForConfirm),
                history.getStateTransit());
        assertEquals(new TicketOwner("emp-001", "张三"), history.getTicketOwner());
        assertEquals(new Operator("emp-002", "李四"), history.getOperator());
        assertNotNull(history.getOperatedAt());
    }
}
