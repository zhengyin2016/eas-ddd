package com.eas.trainingcontext.domain.ticket.service;

import com.eas.trainingcontext.domain.candidate.entity.Candidate;
import com.eas.trainingcontext.domain.candidate.repository.CandidateRepository;
import com.eas.trainingcontext.domain.course.entity.Course;
import com.eas.trainingcontext.domain.course.repository.CourseRepository;
import com.eas.trainingcontext.domain.learning.repository.LearningRepository;
import com.eas.trainingcontext.domain.ticket.entity.Ticket;
import com.eas.trainingcontext.domain.ticket.entity.TicketException;
import com.eas.trainingcontext.domain.ticket.repository.TicketRepository;
import com.eas.trainingcontext.domain.ticket.valueobject.Nominator;
import com.eas.trainingcontext.domain.ticket.valueobject.Nominee;
import com.eas.trainingcontext.domain.tickethistory.repository.TicketHistoryRepository;
import com.eas.trainingcontext.domain.training.entity.Training;
import com.eas.trainingcontext.domain.training.repository.TrainingRepository;

import java.util.List;
import java.util.Optional;

/**
 * 票领域服务（书中20.3.4节）
 *
 * 协调Ticket、TicketHistory、Candidate等多个聚合的跨聚合操作。
 * 服务驱动设计阶段通过序列图发现。
 */
public class TicketService {

    private TicketRepository ticketRepository;
    private TrainingRepository trainingRepository;
    private CourseRepository courseRepository;
    private LearningRepository learningRepository;
    private CandidateRepository candidateRepository;
    private TicketHistoryRepository ticketHistoryRepository;

    public void nominate(String trainingId, String nomineeEmployeeId, String nomineeName,
                         String nominatorEmployeeId, String nominatorName) {
        Training training = trainingRepository.findById(trainingId)
                .orElseThrow(() -> new TicketException("培训不存在"));

        Course course = courseRepository.findById(training.getCourseId())
                .orElseThrow(() -> new TicketException("课程不存在"));

        if (learningRepository.existsByCourseIdAndEmployeeId(course.getId(), nomineeEmployeeId)) {
            throw new TicketException("候选人已经学习过该课程");
        }

        Optional<Ticket> ticketOpt = ticketRepository.findByTrainingIdAndStatusAvailable(trainingId);
        if (ticketOpt.isEmpty()) {
            throw new TicketException("没有可用的培训票");
        }

        Ticket ticket = ticketOpt.get();
        Nominee nominee = new Nominee(nomineeEmployeeId, nomineeName);
        Nominator nominator = new Nominator(nominatorEmployeeId, nominatorName);

        ticket.nominate(nominee, nominator);

        ticketRepository.save(ticket);

        List<Candidate> candidates = candidateRepository.findByTrainingId(trainingId);
        for (Candidate candidate : candidates) {
            if (candidate.getEmployeeId().equals(nomineeEmployeeId)) {
                candidateRepository.remove(candidate.getId());
                break;
            }
        }
    }

    public void setTicketRepository(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    public void setTrainingRepository(TrainingRepository trainingRepository) {
        this.trainingRepository = trainingRepository;
    }

    public void setCourseRepository(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    public void setLearningRepository(LearningRepository learningRepository) {
        this.learningRepository = learningRepository;
    }

    public void setCandidateRepository(CandidateRepository candidateRepository) {
        this.candidateRepository = candidateRepository;
    }

    public void setTicketHistoryRepository(TicketHistoryRepository ticketHistoryRepository) {
        this.ticketHistoryRepository = ticketHistoryRepository;
    }
}
