package com.uet.agent_simulation_api.responses.simulation;

import java.util.List;

public record PigpenDataResponse(
    Integer pigpenId,
    List<PigDataResponse> pigData
) {}
