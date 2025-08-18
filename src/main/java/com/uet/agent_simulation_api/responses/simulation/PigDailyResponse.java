package com.uet.agent_simulation_api.responses.simulation;

public record PigDailyResponse(
    Integer day,
    Double weight,
    Double dfi,
    Double cfi,
    Double targetCfi,
    Double targetDfi,
    Integer eatCount,
    Integer excreteCount,
    Integer seir
) {}
