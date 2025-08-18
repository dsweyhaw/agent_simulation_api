package com.uet.agent_simulation_api.responses.simulation;

import java.math.BigInteger;

public record RunSimulationResponse(
    Integer nodeId,
    BigInteger projectId,
    BigInteger modelId,
    BigInteger experimentId,
    BigInteger experimentResultId,
    Integer order
) {}
