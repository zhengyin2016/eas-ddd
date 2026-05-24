package com.eas.trainingcontext.domain.course.repository;

import com.eas.trainingcontext.domain.course.entity.Course;

import java.util.Optional;

public interface CourseRepository {
    Optional<Course> findById(String id);
}
