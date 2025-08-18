package com.uet.agent_simulation_api.controllers;

import com.uet.agent_simulation_api.responses.ResponseHandler;
import com.uet.agent_simulation_api.responses.SuccessResponse;
import com.uet.agent_simulation_api.services.metric.IMetricService;
import com.uet.agent_simulation_api.services.metric.SimulationMetricService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/metrics")
@RequiredArgsConstructor
public class MetricController {
    private final IMetricService metricService;
    private final SimulationMetricService simulationMetricService;
    private final ResponseHandler responseHandler;

    @GetMapping
    public ResponseEntity<SuccessResponse> get() {
        return responseHandler.respondSuccess(metricService.get());
    }

    @GetMapping("/cpu-usage")
    public ResponseEntity<SuccessResponse> getCpuUsage() {
        return responseHandler.respondSuccess(metricService.getCpuUsage());
    }

    @GetMapping("/ram-usage")
    public ResponseEntity<SuccessResponse> getRamUsage() {
        return responseHandler.respondSuccess(metricService.getRamUsage());
    }

    @GetMapping("/simulation")
    public ResponseEntity<SuccessResponse> getSimulationMetrics(
        @RequestParam(name = "experiment_result_ids") String experimentResultIds
    ) {
        final var result = simulationMetricService.get(experimentResultIds);

        return responseHandler.respondSuccess(result);
    }
}
