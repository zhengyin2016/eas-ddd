package com.eas.trainingcontext.domain.candidate.repository;

import com.eas.trainingcontext.domain.candidate.entity.Candidate;

import java.util.List;

public interface CandidateRepository {
    List<Candidate> findByTrainingId(String trainingId);
    void remove(String id);
}
