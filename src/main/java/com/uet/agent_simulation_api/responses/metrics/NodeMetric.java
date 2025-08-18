package com.uet.agent_simulation_api.responses.metrics;

import java.util.List;

public record NodeMetric(
    Integer nodeId,
    String nodeName,
    List<NodeMetricDataResponse> nodeMetricData
) {}
