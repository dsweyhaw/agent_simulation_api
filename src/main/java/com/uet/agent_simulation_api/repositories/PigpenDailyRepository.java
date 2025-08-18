package com.uet.agent_simulation_api.repositories;

import com.uet.agent_simulation_api.models.PigpenDaily;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface PigpenDailyRepository extends JpaRepository<PigpenDaily, Integer> {
    @Transactional
    @Modifying
    @Query(value = "DELETE FROM PigpenDaily WHERE runId = :run_id")
    void deleteByRunId(@Param("run_id") Integer runId);
}
