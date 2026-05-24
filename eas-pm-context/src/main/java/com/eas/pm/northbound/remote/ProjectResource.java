package com.eas.pm.northbound.remote;

import com.eas.pm.message.CreateProjectRequest;
import com.eas.pm.message.CreateTaskRequest;
import com.eas.pm.message.ProjectResponse;
import com.eas.pm.northbound.appservice.ProjectAppService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 项目REST控制器
 */
@RestController
@RequestMapping("/api/pm/projects")
public class ProjectResource {

    private final ProjectAppService projectAppService;

    public ProjectResource(ProjectAppService projectAppService) {
        this.projectAppService = projectAppService;
    }

    @PostMapping
    public ProjectResponse createProject(@RequestBody CreateProjectRequest request) {
        return projectAppService.createProject(request);
    }

    @PostMapping("/{id}/approve")
    public void approveProject(@PathVariable String id) {
        projectAppService.approveProject(id);
    }

    @PostMapping("/{id}/start")
    public void startProject(@PathVariable String id) {
        projectAppService.startProject(id);
    }

    @PostMapping("/{id}/suspend")
    public void suspendProject(@PathVariable String id) {
        projectAppService.suspendProject(id);
    }

    @PostMapping("/{id}/resume")
    public void resumeProject(@PathVariable String id) {
        projectAppService.resumeProject(id);
    }

    @PostMapping("/{id}/close")
    public void closeProject(@PathVariable String id) {
        projectAppService.closeProject(id);
    }

    @PostMapping("/{id}/tasks")
    public ProjectResponse createTask(@PathVariable String id, @RequestBody CreateTaskRequest request) {
        return projectAppService.createTask(new CreateTaskRequest(id, request.name(), request.assigneeId(), request.estimatedHours()));
    }

    @PostMapping("/{projectId}/tasks/{taskId}/start")
    public void startTask(@PathVariable String projectId, @PathVariable String taskId) {
        projectAppService.startTask(projectId, taskId);
    }

    @PostMapping("/{projectId}/tasks/{taskId}/complete")
    public void completeTask(@PathVariable String projectId, @PathVariable String taskId, @RequestParam int actualHours) {
        projectAppService.completeTask(projectId, taskId, actualHours);
    }

    @GetMapping("/{id}")
    public ProjectResponse getProject(@PathVariable String id) {
        return projectAppService.getProject(id);
    }

    @GetMapping
    public List<ProjectResponse> listProjects() {
        return projectAppService.listProjects();
    }
}
