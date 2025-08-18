package com.uet.agent_simulation_api.repositories;

import com.uet.agent_simulation_api.models.PigpenSyncRun;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface PigpenSyncRunRepository extends JpaRepository<PigpenSyncRun, Integer> {
    @Query(value = "SELECT MAX(runId) FROM PigpenDaily")
    Integer findNewRunId();

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM PigpenSyncRun WHERE runId = :run_id")
    void deleteByRunId(@Param("run_id") Integer runId);
}
