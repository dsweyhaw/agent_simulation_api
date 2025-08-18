package com.uet.agent_simulation_api.requests.experiment_result;

import com.uet.agent_simulation_api.models.Experiment;
import com.uet.agent_simulation_api.models.Node;

import java.math.BigInteger;

public record NewExperimentResult(
    Experiment experiment,
    Long finalStep,
    String pathToLocalExperimentOutputDir,
    BigInteger experimentResultNumber,
    Node node,
    Integer requestNumber
) {}
