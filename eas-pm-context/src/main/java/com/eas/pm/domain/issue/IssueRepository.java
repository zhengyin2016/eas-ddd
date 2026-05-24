package com.eas.pm.domain.issue;

import java.util.List;
import java.util.Optional;

/**
 * 问题仓储端口
 */
public interface IssueRepository {

    Issue save(Issue issue);

    Optional<Issue> findById(IssueId id);

    List<Issue> findByProjectId(String projectId);

    List<Issue> findByAssigneeId(String assigneeId);

    void delete(IssueId id);
}
