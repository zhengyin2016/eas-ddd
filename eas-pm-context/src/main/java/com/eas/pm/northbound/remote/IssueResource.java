package com.eas.pm.northbound.remote;

import com.eas.pm.domain.issue.Issue;
import com.eas.pm.message.CreateIssueRequest;
import com.eas.pm.northbound.appservice.IssueAppService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 问题REST控制器
 */
@RestController
@RequestMapping("/api/pm/issues")
public class IssueResource {

    private final IssueAppService issueAppService;

    public IssueResource(IssueAppService issueAppService) {
        this.issueAppService = issueAppService;
    }

    @PostMapping
    public void createIssue(@RequestBody CreateIssueRequest request) {
        issueAppService.createIssue(request);
    }

    @PostMapping("/{id}/assign")
    public void assignIssue(@PathVariable String id, @RequestParam String assigneeId) {
        issueAppService.assignIssue(id, assigneeId);
    }

    @PostMapping("/{id}/resolve")
    public void resolveIssue(@PathVariable String id, @RequestParam String resolution) {
        issueAppService.resolveIssue(id, resolution);
    }

    @PostMapping("/{id}/close")
    public void closeIssue(@PathVariable String id) {
        issueAppService.closeIssue(id);
    }

    @GetMapping
    public List<Issue> findByProjectId(@RequestParam String projectId) {
        return issueAppService.findByProjectId(projectId);
    }
}
