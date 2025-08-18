package com.uet.agent_simulation_api.services.metric;

import com.uet.agent_simulation_api.responses.metrics.MetricResponse;

public interface IMetricService {
    MetricResponse get();

    Double getCpuUsage();

    Double getRamUsage();
}
