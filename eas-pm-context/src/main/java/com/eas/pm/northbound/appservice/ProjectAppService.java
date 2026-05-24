package com.eas.pm.northbound.appservice;

import com.eas.pm.domain.assignment.AssignmentRepository;
import com.eas.pm.domain.project.*;
import com.eas.pm.message.CreateProjectRequest;
import com.eas.pm.message.CreateTaskRequest;
import com.eas.pm.message.ProjectResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 项目应用服务
 */
@Service
public class ProjectAppService {

    private final ProjectRepository projectRepository;
    private final ProjectDomainService projectDomainService;
    private final AssignmentRepository assignmentRepository;

    public ProjectAppService(ProjectRepository projectRepository,
                             ProjectDomainService projectDomainService,
                             AssignmentRepository assignmentRepository) {
        this.projectRepository = projectRepository;
        this.projectDomainService = projectDomainService;
        this.assignmentRepository = assignmentRepository;
    }

    @Transactional
    public ProjectResponse createProject(CreateProjectRequest request) {
        projectDomainService.validateProjectDates(request.startDate(), request.endDate());
        projectDomainService.checkProjectBudget(request.budget());

        Project project = Project.builder()
            .name(request.name())
            .customerId(request.customerId())
            .contractId(request.contractId())
            .pmId(request.pmId())
            .budget(request.budget())
            .startDate(request.startDate())
            .endDate(request.endDate())
            .build();

        Project saved = projectRepository.save(project);
        return toResponse(saved);
    }

    @Transactional
    public void approveProject(String projectId) {
        Project project = findProject(projectId);
        project.approve();
        projectRepository.save(project);
        // TODO: 发布ProjectApprovedEvent
    }

    @Transactional
    public void startProject(String projectId) {
        Project project = findProject(projectId);
        project.start();
        projectRepository.save(project);
    }

    @Transactional
    public void suspendProject(String projectId) {
        Project project = findProject(projectId);
        project.suspend();
        projectRepository.save(project);
    }

    @Transactional
    public void resumeProject(String projectId) {
        Project project = findProject(projectId);
        project.resume();
        projectRepository.save(project);
    }

    @Transactional
    public void closeProject(String projectId) {
        Project project = findProject(projectId);
        project.close();
        projectRepository.save(project);
        // TODO: 发布ProjectClosedEvent，通知HR释放人员
    }

    @Transactional
    public ProjectResponse createTask(CreateTaskRequest request) {
        Project project = findProject(request.projectId());
        project.createTask(request.name(), request.assigneeId(), request.estimatedHours());
        Project saved = projectRepository.save(project);
        return toResponse(saved);
    }

    @Transactional
    public void startTask(String projectId, String taskId) {
        Project project = findProject(projectId);
        TaskId task_id = TaskId.of(taskId);
        project.getTask(task_id).start();
        projectRepository.save(project);
    }

    @Transactional
    public void completeTask(String projectId, String taskId, int actualHours) {
        Project project = findProject(projectId);
        TaskId task_id = TaskId.of(taskId);
        project.getTask(task_id).complete(actualHours);
        projectRepository.save(project);
    }

    @Transactional(readOnly = true)
    public ProjectResponse getProject(String projectId) {
        Project project = findProject(projectId);
        return toResponse(project);
    }

    @Transactional(readOnly = true)
    public List<ProjectResponse> listProjects() {
        return projectRepository.findAll().stream()
            .map(this::toResponse)
            .toList();
    }

    private Project findProject(String projectId) {
        return projectRepository.findById(ProjectId.of(projectId))
            .orElseThrow(() -> new IllegalArgumentException("Project not found: " + projectId));
    }

    private ProjectResponse toResponse(Project project) {
        return new ProjectResponse(
            project.id().value(),
            project.name(),
            project.customerId(),
            project.contractId(),
            project.pmId(),
            project.status(),
            project.budget(),
            project.startDate(),
            project.endDate(),
            project.tasks().stream()
                .map(t -> new ProjectResponse.TaskSummary(
                    t.id().value(),
                    t.name(),
                    t.assigneeId(),
                    t.status().name(),
                    t.estimatedHours(),
                    t.actualHours()
                )).toList(),
            project.milestones().stream()
                .map(m -> new ProjectResponse.MilestoneSummary(
                    m.id().value(),
                    m.name(),
                    m.plannedDate(),
                    m.actualDate(),
                    m.completed()
                )).toList()
        );
    }
}
