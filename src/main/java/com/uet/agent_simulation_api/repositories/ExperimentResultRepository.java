package com.uet.agent_simulation_api.repositories;

import com.uet.agent_simulation_api.models.ExperimentResult;
import com.uet.agent_simulation_api.models.ExperimentResultImage;
import com.uet.agent_simulation_api.models.projections.ExperimentResultDetailProjection;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

public interface ExperimentResultRepository extends JpaRepository<ExperimentResult, BigInteger> {
    List<ExperimentResult> findByExperimentId(BigInteger experimentId);

    @Query(
        value = """
            SELECT er FROM ExperimentResult er
            JOIN Experiment e ON er.experimentId = e.id
            WHERE e.userId = :user_id
            AND (:experiment_id IS NULL OR e.id = :experiment_id)
            AND (:model_id IS NULL OR e.modelId = :model_id)
            AND (:project_id IS NULL OR e.projectId = :project_id)
            AND (:node_id IS NULL OR er.nodeId = :node_id)
            AND (:experiment_result_number IS NULL OR er.number = :experiment_result_number)
        """
    )
    List<ExperimentResult> find(
        @Param("user_id") BigInteger userId,
        @Param("experiment_id") BigInteger experimentId,
        @Param("model_id") BigInteger modelId,
        @Param("project_id") BigInteger projectId,
        @Param("node_id") Integer nodeId,
        @Param("experiment_result_number") Integer experimentResultNumber);

    @Query(
        value = """
            SELECT er FROM ExperimentResult er
            JOIN Experiment e ON er.experimentId = e.id
            WHERE e.userId = :user_id
            AND er.id = :id
        """
    )
    Optional<ExperimentResult> findByIdAndUserId(
        @Param("id") BigInteger id,
        @Param("user_id") BigInteger userId
    );

    @Query(
        value = """
            SELECT
            er.id AS id,
            er.finalStep AS finalStep,
            er.status AS status,
            er.nodeId AS nodeId,
            e.id AS experimentId,
            m.id AS modelId,
            p.id AS projectId,
            e.name AS experimentName,
            m.name AS modelName,
            p.name AS projectName
            FROM ExperimentResult er
            JOIN Experiment e ON er.experimentId = e.id
            JOIN Model m ON e.modelId = m.id
            JOIN Project p ON m.projectId = p.id
            WHERE e.userId = :user_id
            AND er.id = :id
        """
    )
    Optional<ExperimentResultDetailProjection> findExperimentResultDetail(
        @Param("id") BigInteger id,
        @Param("user_id") BigInteger userId
    );

    @Transactional
    @Modifying
    @Query("DELETE FROM ExperimentResult er WHERE er.experimentId = :experiment_id")
    void deleteByExperimentId(@Param("experiment_id") BigInteger experimentId);

    @Transactional
    @Modifying
    @Query("""
        DELETE FROM ExperimentResult er
        WHERE er.experimentId = :experiment_id
        AND er.number = :experiment_result_number
        AND er.nodeId = :node_id
    """)
    void deleteByExperimentIdAndExperimentResultNumber(
        @Param("experiment_id") BigInteger experimentId,
        @Param("experiment_result_number") Integer experimentResultNumber,
        @Param("node_id") Integer nodeId
    );

    @Transactional
    @Modifying
    @Query("""
        DELETE FROM ExperimentResult er
        WHERE CONCAT(er.experimentId, '-', er.number, '-', er.nodeId) IN :delete_data
    """)
    void deleteByExperimentIdAndExperimentResultNumberAndNodeId(@Param("delete_data") List<String> deleteData);

    @Transactional
    @Modifying
    @Query("""
        DELETE FROM ExperimentResult er
        WHERE CONCAT(er.experimentId, '-', er.nodeId) IN :delete_data
    """)
    void deleteByExperimentIdAndNodeId(@Param("delete_data") List<String> deleteData);

    @Query(
        value = """
            SELECT MAX(er.number) FROM ExperimentResult er
            JOIN Experiment e ON er.experimentId = e.id
            WHERE er.experimentId = :experiment_id
            AND e.userId = :user_id
        """
    )
    BigInteger getLastExperimentResultNumber(
        @Param("experiment_id") BigInteger experimentId,
        @Param("user_id") BigInteger userId
    );

    @Query(
        value = """
            SELECT er FROM ExperimentResult er
            WHERE er.simulationRunId = :simulation_run_id
        """
    )
    List<ExperimentResult> findBySimulationRunId(@Param("simulation_run_id") BigInteger simulationRunId);

    @Transactional
    @Modifying
    @Query("DELETE FROM ExperimentResult er WHERE er.simulationRunId = :simulation_run_id")
    void deleteBySimulationRunId(@Param("simulation_run_id") BigInteger simulationRunId);

    @Transactional
    @Modifying
    @Query("DELETE FROM ExperimentResult er WHERE er.id IN :ids")
    void deleteByIds(@Param("ids") List<BigInteger> ids);

    @Query(
        value = """
            SELECT er.simulationRunId FROM ExperimentResult er
            WHERE er.id = :experiment_result_id
        """
    )
    Integer getSimulationRunId(@Param("experiment_result_id") Integer experimentResultId);
}
