package com.uet.agent_simulation_api.responses.metrics;

public record NodeMetricEndpointResponse(
   String nodeName,
   String showCpuUsageEndpoint,
    String showMemoryUsageEndpoint
) {}
