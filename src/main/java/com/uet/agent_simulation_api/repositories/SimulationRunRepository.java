package com.uet.agent_simulation_api.repositories;

import com.uet.agent_simulation_api.models.SimulationRun;
import com.uet.agent_simulation_api.models.projections.SimulationRunProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigInteger;
import java.util.List;

public interface SimulationRunRepository extends JpaRepository<SimulationRun, BigInteger> {
    @Query(
        """
            SELECT
                sr.id AS id,
                er.id AS experimentResultId,
                e.name AS experimentName,
                m.name AS modelName,
                er.finalStep AS finalStep,
                sr.createdAt AS createdAt
            FROM ExperimentResult er
            JOIN SimulationRun sr ON er.simulationRunId = sr.id
            JOIN Experiment e ON er.experimentId = e.id
            JOIN Model m ON e.modelId = m.id
            WHERE e.projectId = :project_id
            AND e.userId = :user_id
            ORDER BY sr.createdAt DESC
        """
    )
    List<SimulationRunProjection> getSimulationHistory(
        @Param("project_id") BigInteger projectId,
        @Param("user_id") BigInteger userId
    );
}
