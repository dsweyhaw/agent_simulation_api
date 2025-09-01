package com.uet.agent_simulation_api.repositories;

import com.uet.agent_simulation_api.models.Experiment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
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
    
    /**
     * Delete all experiments by model ID and user ID (for security)
     * @param modelId The model ID
     * @param userId The user ID (for security check)
     * @return number of deleted records
     */
    @Modifying
    @Query(
        value = """
            DELETE FROM Experiment e
            WHERE e.modelId = :model_id AND e.userId = :user_id
        """
    )
    int deleteAllByModelIdAndUserId(
        @Param("model_id") BigInteger modelId, 
        @Param("user_id") BigInteger userId
    );
    
    /**
     * Delete all experiments by project ID and user ID (for security)
     * @param projectId The project ID
     * @param userId The user ID (for security check)
     * @return number of deleted records
     */
    @Modifying
    @Query(
        value = """
            DELETE FROM Experiment e
            WHERE e.projectId = :project_id AND e.userId = :user_id
        """
    )
    int deleteAllByProjectIdAndUserId(
        @Param("project_id") BigInteger projectId, 
        @Param("user_id") BigInteger userId
    );
}
