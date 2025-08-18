package com.uet.agent_simulation_api.services.simulation;

import com.uet.agent_simulation_api.models.SimulationRun;
import com.uet.agent_simulation_api.responses.simulation.SimulationHistoryResponse;

import java.math.BigInteger;
import java.util.List;

public interface ISimulationRunService {
    SimulationRun create();

    List<SimulationHistoryResponse> getSimulationHistory(BigInteger projectId);

    void delete(BigInteger simulationId);
}
