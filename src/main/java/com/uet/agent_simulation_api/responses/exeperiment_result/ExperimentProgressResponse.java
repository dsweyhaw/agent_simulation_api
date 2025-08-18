package com.uet.agent_simulation_api.responses.exeperiment_result;

public record ExperimentProgressResponse(
    Boolean waiting,
    Integer status,
    Integer currentStep,
    Integer finalStep
) {}
