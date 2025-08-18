package com.uet.agent_simulation_api.repositories;

import com.uet.agent_simulation_api.models.PigDataDaily;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.List;

public interface PigDataDailyRepository extends JpaRepository<PigDataDaily, BigInteger> {
    @Transactional
    @Modifying
    @Query("DELETE FROM PigDataDaily WHERE runId = :run_id")
    void deleteByRunId(@Param("run_id") Integer runId);

    @Query("SELECT p FROM PigDataDaily p WHERE p.runId = :runId")
    List<PigDataDaily> findAllByRunId(@Param("runId") BigInteger runId);
}
