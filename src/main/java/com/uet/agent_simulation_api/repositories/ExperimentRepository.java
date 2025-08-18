package com.uet.agent_simulation_api.repositories;

import com.uet.agent_simulation_api.models.Experiment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

public interface ExperimentRepository extends JpaRepository<Experiment, BigInteger> {
    @Query(
        value = """
            SELECT e FROM Experiment e
            WHERE e.id = :experiment_id AND e.modelId = :model_id
        """
    )
    Optional<Experiment> findByExperimentIdAndModelId(
        @Param("experiment_id") BigInteger experimentId,
        @Param("model_id") BigInteger modelId
    );

    @Query(
        value = """
            SELECT e FROM Experiment e
            WHERE e.userId = :user_id
            AND (:project_id IS NULL OR e.projectId = :project_id)
            AND (:model_id IS NULL OR e.modelId = :model_id)
        """
    )
    List<Experiment> find(
        @Param("user_id") BigInteger userId,
        @Param("project_id") BigInteger projectId,
        @Param("model_id") BigInteger modelId
    );

    @Query(
        value = """
            SELECT e FROM Experiment e
            JOIN Model m ON e.modelId = m.id
            WHERE m.name LIKE %:name%
        """
    )
    Experiment findByModelName(@Param("name") String name);
}
