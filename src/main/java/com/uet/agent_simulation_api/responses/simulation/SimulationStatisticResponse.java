package com.uet.agent_simulation_api.responses.simulation;

import java.util.List;

public record SimulationStatisticResponse(
    List<PigpenDataResponse> pigpenData
) {}
