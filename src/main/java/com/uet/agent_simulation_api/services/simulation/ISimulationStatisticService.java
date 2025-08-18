package com.uet.agent_simulation_api.services.simulation;

import com.uet.agent_simulation_api.responses.simulation.SimulationStatisticResponse;

public interface ISimulationStatisticService {
    SimulationStatisticResponse getSimulationStatistic(
        String experimentResultIds,
        String pigpenIds,
        String pigIds
    );
}
