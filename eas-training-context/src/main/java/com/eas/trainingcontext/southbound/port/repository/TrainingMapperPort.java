package com.eas.trainingcontext.southbound.port.repository;

import com.eas.trainingcontext.domain.training.entity.Training;
import org.apache.ibatis.annotations.Mapper;

import java.util.Optional;

@Mapper
public interface TrainingMapperPort {
    Optional<Training> findById(String id);
    void save(Training training);
}
