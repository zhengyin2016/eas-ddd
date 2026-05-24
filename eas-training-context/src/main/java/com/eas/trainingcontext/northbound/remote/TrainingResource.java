package com.eas.trainingcontext.northbound.remote;

import com.eas.dddcore.Resources;
import com.eas.trainingcontext.domain.training.entity.Training;
import com.eas.trainingcontext.domain.training.repository.TrainingRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 培训REST资源（书中20.3.5节）
 */
@RestController
@RequestMapping("/api/trainings")
public class TrainingResource {

    private final TrainingRepository trainingRepository;

    public TrainingResource(TrainingRepository trainingRepository) {
        this.trainingRepository = trainingRepository;
    }

    @GetMapping("/{trainingId}")
    public ResponseEntity findById(@PathVariable String trainingId) {
        return Resources.execute(() ->
                trainingRepository.findById(trainingId), "查询培训");
    }
}
