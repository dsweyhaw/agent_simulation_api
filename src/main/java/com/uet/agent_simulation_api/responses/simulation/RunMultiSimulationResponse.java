package com.uet.agent_simulation_api.responses.simulation;

import java.math.BigInteger;

public record RunMultiSimulationResponse(
    Integer nodeId,
    BigInteger projectId,
    BigInteger modelId,
    BigInteger experimentId,
    BigInteger experimentResultId,
    Integer order,
    String nodeName,
    String projectName,
    String modelName,
    String experimentName,
    String title
) {}
