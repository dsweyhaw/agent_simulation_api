package com.uet.agent_simulation_api.responses.metrics;

import java.util.List;

public record MetricResponse(
   List<NodeMetricEndpointResponse> nodeMetrics
) {}
