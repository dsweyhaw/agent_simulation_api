package com.uet.agent_simulation_api.responses.metrics;

import java.math.BigInteger;

public record NodeMetricDataResponse(
    BigInteger id,
    Double cpuUsage,
    Double memoryUsage,
    Double systemMemory,
    Integer duration
) {}
