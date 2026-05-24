package com.eas.trainingcontext.domain.ticket;

import com.eas.trainingcontext.domain.candidate.entity.Candidate;
import com.eas.trainingcontext.domain.candidate.repository.CandidateRepository;
import com.eas.trainingcontext.domain.course.entity.Course;
import com.eas.trainingcontext.domain.course.repository.CourseRepository;
import com.eas.trainingcontext.domain.learning.repository.LearningRepository;
import com.eas.trainingcontext.domain.ticket.entity.Ticket;
import com.eas.trainingcontext.domain.ticket.entity.TicketException;
import com.eas.trainingcontext.domain.ticket.repository.TicketRepository;
import com.eas.trainingcontext.domain.ticket.service.TicketService;
import com.eas.trainingcontext.domain.tickethistory.repository.TicketHistoryRepository;
import com.eas.trainingcontext.domain.training.entity.Training;
import com.eas.trainingcontext.domain.training.repository.TrainingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * 票领域服务测试驱动开发（书中20.3.4节）
 *
 * 两个测试用例：
 * 1. 没有符合条件的Ticket，抛出TicketException
 * 2. 培训票被成功提名给候选人
 *
 * 使用Mockito mock Repository验证领域服务与资源库的协作。
 */
public class TicketServiceTest {

    private TicketService ticketService;
    private TicketRepository ticketRepository;
    private TrainingRepository trainingRepository;
    private CourseRepository courseRepository;
    private LearningRepository learningRepository;
    private CandidateRepository candidateRepository;
    private TicketHistoryRepository ticketHistoryRepository;

    @BeforeEach
    public void setUp() {
        ticketRepository = mock(TicketRepository.class);
        trainingRepository = mock(TrainingRepository.class);
        courseRepository = mock(CourseRepository.class);
        learningRepository = mock(LearningRepository.class);
        candidateRepository = mock(CandidateRepository.class);
        ticketHistoryRepository = mock(TicketHistoryRepository.class);

        ticketService = new TicketService(ticketRepository, trainingRepository,
                courseRepository, learningRepository, candidateRepository, ticketHistoryRepository);
    }

    @Test
    public void should_throw_ticket_exception_when_no_available_ticket() {
        Training training = new Training("training-1", "course-1");
        when(trainingRepository.findById("training-1")).thenReturn(Optional.of(training));
        when(courseRepository.findById("course-1")).thenReturn(Optional.of(new Course("course-1", "DDD")));
        when(learningRepository.existsByCourseIdAndEmployeeId("course-1", "emp-001")).thenReturn(false);
        when(ticketRepository.findByTrainingIdAndStatusAvailable("training-1")).thenReturn(Optional.empty());

        assertThrows(TicketException.class, () -> {
            ticketService.nominate("training-1", "emp-001", "张三", "emp-002", "李四");
        });

        verify(ticketRepository, never()).save(any());
    }

    @Test
    public void should_nominate_successfully_when_ticket_available() {
        Training training = new Training("training-1", "course-1");
        Ticket ticket = new Ticket("ticket-1", "training-1");
        Candidate candidate = new Candidate("candidate-1", "training-1", "emp-001");

        when(trainingRepository.findById("training-1")).thenReturn(Optional.of(training));
        when(courseRepository.findById("course-1")).thenReturn(Optional.of(new Course("course-1", "DDD")));
        when(learningRepository.existsByCourseIdAndEmployeeId("course-1", "emp-001")).thenReturn(false);
        when(ticketRepository.findByTrainingIdAndStatusAvailable("training-1")).thenReturn(Optional.of(ticket));
        when(candidateRepository.findByTrainingId("training-1")).thenReturn(List.of(candidate));

        ticketService.nominate("training-1", "emp-001", "张三", "emp-002", "李四");

        verify(ticketHistoryRepository).save(any());
        verify(ticketRepository).save(ticket);
        verify(candidateRepository).remove("candidate-1");
    }
}
