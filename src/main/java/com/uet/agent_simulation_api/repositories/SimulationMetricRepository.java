package com.uet.agent_simulation_api.repositories;

import com.uet.agent_simulation_api.models.SimulationMetric;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;

public interface SimulationMetricRepository extends JpaRepository<SimulationMetric, BigInteger> {
    @Transactional
    @Modifying
    @Query(
        value = """
            DELETE FROM SimulationMetric sm
            WHERE sm.simulationRunId = :simulation_run_id
        """
    )
    void deleteBySimulationRunId(@Param("simulation_run_id") BigInteger simulationRunId);
}
