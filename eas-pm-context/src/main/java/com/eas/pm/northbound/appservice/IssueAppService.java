package com.eas.pm.northbound.appservice;

import com.eas.pm.domain.issue.*;
import com.eas.pm.message.CreateIssueRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 问题应用服务
 */
@Service
public class IssueAppService {

    private final IssueRepository issueRepository;

    public IssueAppService(IssueRepository issueRepository) {
        this.issueRepository = issueRepository;
    }

    @Transactional
    public void createIssue(CreateIssueRequest request) {
        Issue issue = new Issue(
            IssueId.generate(),
            request.projectId(),
            request.title(),
            request.description(),
            request.severity(),
            request.priority()
        );
        issueRepository.save(issue);
        // TODO: 发布IssueCreatedEvent
    }

    @Transactional
    public void assignIssue(String issueId, String assigneeId) {
        Issue issue = findIssue(issueId);
        issue.assign(assigneeId);
        issueRepository.save(issue);
    }

    @Transactional
    public void resolveIssue(String issueId, String resolution) {
        Issue issue = findIssue(issueId);
        issue.resolve(resolution);
        issueRepository.save(issue);
    }

    @Transactional
    public void closeIssue(String issueId) {
        Issue issue = findIssue(issueId);
        issue.close();
        issueRepository.save(issue);
    }

    @Transactional(readOnly = true)
    public List<Issue> findByProjectId(String projectId) {
        return issueRepository.findByProjectId(projectId);
    }

    private Issue findIssue(String issueId) {
        return issueRepository.findById(IssueId.of(issueId))
            .orElseThrow(() -> new IllegalArgumentException("Issue not found: " + issueId));
    }
}
