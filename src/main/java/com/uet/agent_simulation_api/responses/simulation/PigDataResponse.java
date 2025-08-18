package com.uet.agent_simulation_api.responses.simulation;

import java.util.List;

public record PigDataResponse(
    Integer pigId,
    List<PigDailyResponse> pigDaily
) {}
