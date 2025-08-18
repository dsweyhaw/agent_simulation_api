package com.uet.agent_simulation_api.models.projections;

import java.math.BigInteger;

public interface ExperimentResultDetailProjection {
    BigInteger getId();
    Integer getFinalStep();
    Integer getStatus();
    BigInteger getExperimentId();
    BigInteger getModelId();
    BigInteger getProjectId();
    Integer getNodeId();
    String getExperimentName();
    String getModelName();
    String getProjectName();
}
