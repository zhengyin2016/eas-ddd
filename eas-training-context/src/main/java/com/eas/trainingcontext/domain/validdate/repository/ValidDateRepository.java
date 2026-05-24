package com.eas.trainingcontext.domain.validdate.repository;

import com.eas.trainingcontext.domain.validdate.entity.ValidDate;

import java.util.List;

public interface ValidDateRepository {
    List<ValidDate> findByTrainingId(String trainingId);
}
