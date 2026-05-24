package com.eas.hr.domain.event;

import com.eas.common.ddd.DomainEvent;

/**
 * 培训计划发布领域事件
 */
public class TrainingPublishedEvent extends DomainEvent {

    private final String trainingName;
    private final int capacity;

    public TrainingPublishedEvent(String aggregateId, String trainingName, int capacity) {
        super(aggregateId, "TrainingPlan");
        this.trainingName = trainingName;
        this.capacity = capacity;
    }

    public String getTrainingName() {
        return trainingName;
    }

    public int getCapacity() {
        return capacity;
    }
}
