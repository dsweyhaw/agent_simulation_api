package com.uet.agent_simulation_api.responses.exeperiment_result;

import java.math.BigInteger;

public record ExperimentResultDetailResponse (
    BigInteger id,
    Integer finalStep,
    Integer status,
    BigInteger experimentId,
    BigInteger modelId,
    BigInteger projectId,
    Integer nodeId,
    String experimentName,
    String modelName,
    String projectName,
    String downloadUrl
) {}
