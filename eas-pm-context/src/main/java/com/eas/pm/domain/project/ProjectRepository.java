package com.eas.pm.domain.project;

import java.util.List;
import java.util.Optional;

/**
 * 项目仓储端口
 */
public interface ProjectRepository {

    Project save(Project project);

    Optional<Project> findById(ProjectId id);

    Optional<Project> findById(String id);

    List<Project> findAll();

    void delete(ProjectId id);
}
