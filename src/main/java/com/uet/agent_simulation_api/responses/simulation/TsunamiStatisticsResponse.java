package com.uet.agent_simulation_api.responses.simulation;

import java.util.List;
import java.util.Map;

public record TsunamiStatisticsResponse(
    List<TsunamiStepData> steps
) {
    public record TsunamiStepData(
        int id,
        Map<String, TsunamiVariable> variables
    ) {}
    
    public record TsunamiVariable(
        String name,
        String type,
        String value
    ) {}
}

