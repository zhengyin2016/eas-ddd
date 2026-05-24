package com.eas.trainingcontext.northbound.remote;

import com.eas.dddcore.Resources;
import com.eas.trainingcontext.northbound.appservice.TrainingAppService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 培训REST资源（书中20.3.5节）
 */
@RestController
@RequestMapping("/api/trainings")
public class TrainingResource {

    private final TrainingAppService trainingAppService;

    public TrainingResource(TrainingAppService trainingAppService) {
        this.trainingAppService = trainingAppService;
    }

    @GetMapping("/{trainingId}")
    public ResponseEntity<?> findById(@PathVariable String trainingId) {
        return Resources.execute(() ->
                trainingAppService.findById(trainingId), "查询培训");
    }
}
