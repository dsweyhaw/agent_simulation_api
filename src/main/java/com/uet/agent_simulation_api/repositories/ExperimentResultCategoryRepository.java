package com.uet.agent_simulation_api.repositories;

import com.uet.agent_simulation_api.models.ExperimentResultCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigInteger;
import java.util.List;

public interface ExperimentResultCategoryRepository extends JpaRepository<ExperimentResultCategory, BigInteger> {
    @Query(
        value = """
            SELECT erc FROM ExperimentResultCategory erc
            JOIN ExperimentResult er ON erc.experimentResultId = er.id
            JOIN Experiment e ON er.experimentId = e.id
            WHERE e.userId = :user_id
            AND (:experiment_result_id IS NULL OR er.id = :experiment_result_id)
            AND (:experiment_id IS NULL OR e.id = :experiment_id)
            AND (:model_id IS NULL OR e.modelId = :model_id)
            AND (:project_id IS NULL OR e.projectId = :project_id)
        """
    )
    List<ExperimentResultCategory> find(
        @Param("experiment_result_id") BigInteger experimentResultId,
        @Param("experiment_id") BigInteger experimentId,
        @Param("model_id") BigInteger modelId,
        @Param("project_id") BigInteger projectId,
        @Param("user_id") BigInteger userId
    );
}
