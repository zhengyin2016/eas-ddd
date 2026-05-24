package com.eas.trainingcontext.domain.learning.repository;

public interface LearningRepository {
    boolean existsByCourseIdAndEmployeeId(String courseId, String employeeId);
}
