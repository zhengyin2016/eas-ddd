package com.eas.trainingcontext.domain.training.repository;

import com.eas.trainingcontext.domain.training.entity.Training;

import java.util.Optional;

public interface TrainingRepository {
    Optional<Training> findById(String id);
    void save(Training training);
}
