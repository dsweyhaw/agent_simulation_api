package com.uet.agent_simulation_api.models.projections;

import java.math.BigInteger;

public interface ExperimentResultImageDetailProjection {
    BigInteger getId();
    Integer getNodeId();
    String getLocation();
    String getExtension();
}
