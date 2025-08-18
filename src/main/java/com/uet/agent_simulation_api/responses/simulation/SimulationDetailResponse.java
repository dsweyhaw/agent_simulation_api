package com.uet.agent_simulation_api.responses.simulation;

import java.math.BigInteger;

public record SimulationDetailResponse (
    BigInteger experimentResultId,
    String experimentName,
    String modelName,
    Long finalStep
) {}
