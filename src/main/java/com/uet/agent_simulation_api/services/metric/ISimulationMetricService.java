package com.uet.agent_simulation_api.services.metric;

import com.uet.agent_simulation_api.responses.metrics.SimulationRunMetricResponse;

public interface ISimulationMetricService {
    SimulationRunMetricResponse get(String experimentResultIds);
}
