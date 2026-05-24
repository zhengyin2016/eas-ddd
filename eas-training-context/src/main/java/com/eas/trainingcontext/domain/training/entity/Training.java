package com.eas.trainingcontext.domain.training.entity;

import com.eas.dddcore.AggregateRoot;
import com.eas.trainingcontext.domain.training.valueobject.Coordinator;
import com.eas.trainingcontext.domain.training.valueobject.ProgramOwner;
import com.eas.trainingcontext.domain.training.valueobject.Teacher;

import java.util.ArrayList;
import java.util.List;

public class Training extends AggregateRoot<String> {

    private String courseId;
    private ProgramOwner programOwner;
    private Coordinator coordinator;
    private Teacher teacher;
    private List<String> candidateIds = new ArrayList<>();

    public Training() {
    }

    public Training(String id, String courseId) {
        this.id = id;
        this.courseId = courseId;
    }

    public void removeCandidate(String employeeId) {
        candidateIds.remove(employeeId);
    }

    public boolean hasCandidate(String employeeId) {
        return candidateIds.contains(employeeId);
    }

    public String getCourseId() { return courseId; }
    public ProgramOwner getProgramOwner() { return programOwner; }
    public Coordinator getCoordinator() { return coordinator; }
    public Teacher getTeacher() { return teacher; }
    public List<String> getCandidateIds() { return candidateIds; }
}
