package com.uet.agent_simulation_api.responses.metrics;

public record ExperimentResultMetricResponse(
    Integer nodeId,
    String nodeName,
    String projectName,
    String modelName,
    String experimentName,
    Integer finalStep,
    Long runTime
) {}
