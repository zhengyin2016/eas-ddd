package com.eas.trainingcontext.domain.course.entity;

import com.eas.dddcore.AggregateRoot;

public class Course extends AggregateRoot<String> {

    private String name;
    private String description;

    public Course() {
    }

    public Course(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getName() { return name; }
    public String getDescription() { return description; }
}
