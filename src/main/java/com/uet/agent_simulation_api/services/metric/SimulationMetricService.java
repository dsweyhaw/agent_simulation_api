package com.uet.agent_simulation_api.services.metric;

import com.uet.agent_simulation_api.models.ExperimentResult;
import com.uet.agent_simulation_api.models.SimulationMetric;
import com.uet.agent_simulation_api.repositories.*;
import com.uet.agent_simulation_api.responses.metrics.ExperimentResultMetricResponse;
import com.uet.agent_simulation_api.responses.metrics.NodeMetric;
import com.uet.agent_simulation_api.responses.metrics.NodeMetricDataResponse;
import com.uet.agent_simulation_api.responses.metrics.SimulationRunMetricResponse;
import com.uet.agent_simulation_api.services.auth.IAuthService;
import com.uet.agent_simulation_api.services.node.NodeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SimulationMetricService implements ISimulationMetricService {
    private final IAuthService authService;
    private final ExperimentResultRepository experimentResultRepository;
    private final SimulationMetricRepository simulationMetricRepository;
    private final NodeService nodeService;

    @Override
    public SimulationRunMetricResponse get(String experimentResultIds) {
        if (experimentResultIds == null) {
            return null;
        }

        final var experimentResultIdList = Arrays.stream(experimentResultIds.split(","))
                .map(Integer::parseInt)
                .toList();
        final var simulationRunId = experimentResultRepository.getSimulationRunId(experimentResultIdList.getFirst());

        // Get all simulation metrics for this run
        final var metrics = simulationMetricRepository.findAll().stream()
                .filter(m -> m.getSimulationRunId().equals(BigInteger.valueOf(simulationRunId)))
                .collect(Collectors.groupingBy(SimulationMetric::getNodeId));

        // Transform metrics data by node
        final var nodeMetrics = metrics.entrySet().stream()
                .map(entry -> {
                    final var nodeId = entry.getKey();
                    final var nodeMetricList = entry.getValue();
                    final double systemMemory = (nodeId.equals(1)) ? 16L * 1024 * 1024 * 1024 : 8L * 1024 * 1024 * 1024;

                    final var metricData = nodeMetricList.stream()
                            .map(m -> new NodeMetricDataResponse(
                                    m.getId(),
                                    m.getCpuUsage(),
                                    m.getRamUsage(),
                                    systemMemory,
                                    m.getDuration()
                            ))
                            .collect(Collectors.toList());

                    return new NodeMetric(
                            nodeId,
                            nodeMetricList.getFirst().getNodeName(),
                            metricData
                    );
                })
                .toList();

        // Get experiment metrics using single optimized query
        final var experimentResultMetrics = experimentResultIdList.stream()
                .map(id -> experimentResultRepository.findExperimentResultDetail(BigInteger.valueOf(id), authService.getCurrentUserId()))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(detail -> new ExperimentResultMetricResponse(
                        detail.getNodeId(),
                        nodeService.getNodeById(detail.getNodeId()).getName(),
                        detail.getProjectName(),
                        detail.getModelName(),
                        detail.getExperimentName(),
                        detail.getFinalStep(),
                        experimentResultRepository.findById(detail.getId())
                                .map(ExperimentResult::getRunTime)
                                .orElse(null)
                ))
                .toList();

        return new SimulationRunMetricResponse(nodeMetrics, experimentResultMetrics);
    }
}
