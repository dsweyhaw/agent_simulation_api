package com.uet.agent_simulation_api.services.multi_simulation;

import com.uet.agent_simulation_api.repositories.PigpenDailyRepository;
import com.uet.agent_simulation_api.repositories.PigpenSyncRunRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MultiSimulationService implements IMultiSimulationService {
    private final PigpenDailyRepository pigpenDailyRepository;
    private final PigpenSyncRunRepository pigpenSyncRunRepository;

    @Override
    public Integer getNewRunId() {
        return pigpenSyncRunRepository.findNewRunId();
    }

    @Override
    public void clear() {
        pigpenSyncRunRepository.deleteAll();
        pigpenDailyRepository.deleteAll();
    }
}
