package com.eas.pm.southbound.adapter.repository;

import com.eas.pm.domain.project.Project;
import com.eas.pm.domain.project.ProjectId;
import com.eas.pm.domain.project.ProjectRepository;
import com.eas.pm.domain.project.Task;
import com.eas.pm.domain.project.Milestone;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Project仓储实现
 */
@Repository
public class ProjectRepositoryImpl implements ProjectRepository {

    private final ProjectMapper projectMapper;

    public ProjectRepositoryImpl(ProjectMapper projectMapper) {
        this.projectMapper = projectMapper;
    }

    @Override
    public Project save(Project project) {
        if (project.id() == null) {
            // 新增
            projectMapper.insert(project);
        } else {
            // 更新
            int updated = projectMapper.update(project);
            if (updated == 0) {
                throw new org.springframework.orm.OptimisticLockingFailureException(
                    "Project update failed due to concurrent modification"
                );
            }
        }
        return project;
    }

    @Override
    public Optional<Project> findById(ProjectId id) {
        return Optional.ofNullable(id).flatMap(i -> findById(i.value()));
    }

    @Override
    public Optional<Project> findById(String id) {
        ProjectDO projectDO = projectMapper.findById(id);
        if (projectDO == null) {
            return Optional.empty();
        }

        // 转换为领域模型（简化版，不加载Task和Milestone）
        Project project = Project.builder()
            .id(ProjectId.of(projectDO.id()))
            .name(projectDO.name())
            .customerId(projectDO.customerId())
            .contractId(projectDO.contractId())
            .pmId(projectDO.pmId())
            .budget(projectDO.budget())
            .startDate(projectDO.startDate())
            .endDate(projectDO.endDate())
            .build();

        // 设置状态和版本（通过反射或添加setter）
        project.setVersion(projectDO.version());

        return Optional.of(project);
    }

    @Override
    public List<Project> findAll() {
        List<ProjectDO> projectDOs = projectMapper.findAll();
        return projectDOs.stream()
            .map(do_ -> {
                Project project = Project.builder()
                    .id(ProjectId.of(do_.id()))
                    .name(do_.name())
                    .customerId(do_.customerId())
                    .contractId(do_.contractId())
                    .pmId(do_.pmId())
                    .budget(do_.budget())
                    .startDate(do_.startDate())
                    .endDate(do_.endDate())
                    .build();
                project.setVersion(do_.version());
                return project;
            })
            .toList();
    }

    @Override
    public void delete(ProjectId id) {
        projectMapper.deleteById(id.value());
    }
}
