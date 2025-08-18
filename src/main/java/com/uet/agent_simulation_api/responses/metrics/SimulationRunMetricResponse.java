package com.uet.agent_simulation_api.responses.metrics;

import java.util.List;

public record SimulationRunMetricResponse(
    List<NodeMetric> nodeMetrics,
    List<ExperimentResultMetricResponse> experimentResultMetrics
) {}
