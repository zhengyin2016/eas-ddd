package com.eas.trainingcontext.southbound.adapter.repository;

import com.eas.trainingcontext.domain.training.entity.Training;
import com.eas.trainingcontext.domain.training.repository.TrainingRepository;
import com.eas.trainingcontext.southbound.port.repository.TrainingMapperPort;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class TrainingRepositoryAdapter implements TrainingRepository {

    private final TrainingMapperPort mapperPort;

    public TrainingRepositoryAdapter(TrainingMapperPort mapperPort) {
        this.mapperPort = mapperPort;
    }

    @Override
    public Optional<Training> findById(String id) {
        return mapperPort.findById(id);
    }

    @Override
    public void save(Training training) {
        mapperPort.save(training);
    }
}
