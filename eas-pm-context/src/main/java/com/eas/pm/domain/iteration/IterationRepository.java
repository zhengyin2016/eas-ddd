package com.eas.pm.domain.iteration;

import java.util.List;
import java.util.Optional;

/**
 * 迭代仓储端口
 */
public interface IterationRepository {

    Iteration save(Iteration iteration);

    Optional<Iteration> findById(IterationId id);

    List<Iteration> findByProjectId(String projectId);

    void delete(IterationId id);
}
