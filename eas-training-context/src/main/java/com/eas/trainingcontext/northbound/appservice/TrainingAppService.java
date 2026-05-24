package com.eas.trainingcontext.northbound.appservice;

import com.eas.trainingcontext.domain.training.entity.Training;
import com.eas.trainingcontext.domain.training.repository.TrainingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * 培训应用服务（书中20.3.5节）
 *
 * 远程服务不应直接访问资源库，应通过应用服务间接访问。
 */
@Service
@Transactional
public class TrainingAppService {

    private final TrainingRepository trainingRepository;

    public TrainingAppService(TrainingRepository trainingRepository) {
        this.trainingRepository = trainingRepository;
    }

    public Optional<Training> findById(String trainingId) {
        return trainingRepository.findById(trainingId);
    }
}
