package com.uet.agent_simulation_api.repositories;

import com.uet.agent_simulation_api.models.Model;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

public interface ModelRepository extends JpaRepository<Model, BigInteger> {
    @Query(
        value = """
            SELECT m FROM Model m
            WHERE m.id = :model_id AND m.projectId = :project_id
        """
    )
    Optional<Model> findByModeIdAndProjectId(
        @Param("model_id") BigInteger modelId,
        @Param("project_id") BigInteger projectId
    );

    @Query(
        value = """
            SELECT m FROM Model m
            WHERE m.userId = :user_id
            AND (:project_id IS NULL OR m.projectId = :project_id)
        """
    )
    List<Model> find(
        @Param("user_id") BigInteger userId,
        @Param("project_id") BigInteger projectId
    );

    @Query(
        value = """
            SELECT m FROM Model m
            JOIN Experiment e ON m.id = e.modelId
            WHERE m.userId = :user_id
            AND (:project_id IS NULL OR m.projectId = :project_id)
            AND e.id IS NOT NULL
        """
    )
    List<Model> findByExperimentNotNull(
        @Param("user_id") BigInteger userId,
        @Param("project_id") BigInteger projectId
    );

    @Query(
        value = """
            SELECT m FROM Model m
            WHERE m.name LIKE %:name%
        """
    )
    Model findByName(@Param("name") String name);
}
