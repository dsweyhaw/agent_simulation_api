package com.uet.agent_simulation_api.services.metric;

import com.uet.agent_simulation_api.models.SimulationMetric;
import com.uet.agent_simulation_api.repositories.SimulationMetricRepository;
import com.uet.agent_simulation_api.services.node.INodeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class MetricsCollectionService {
    private final IMetricService metricService;
    private final INodeService nodeService;
    private final SimulationMetricRepository simulationMetricRepository;
    private final Map<BigInteger, ScheduledFuture<?>> metricCollectors = new ConcurrentHashMap<>();
    private static long collectionIntervalMs = 3000;

    @Scheduled(fixedDelayString = "${metrics.cleanup.interval:60000}")
    public void cleanupStoppedCollectors() {
        metricCollectors.entrySet().removeIf(entry -> entry.getValue().isCancelled());
    }

    @Async
    public void startCollecting(BigInteger simulationRunId) {
        if (metricCollectors.containsKey(simulationRunId)) {
            return;
        }

        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        ScheduledFuture<?> collector = scheduler.scheduleAtFixedRate(
                () -> collectMetrics(simulationRunId),
                0,
                collectionIntervalMs,
                TimeUnit.MILLISECONDS
        );

        metricCollectors.put(simulationRunId, collector);
    }

    public void stopCollecting(BigInteger simulationRunId) {
        ScheduledFuture<?> collector = metricCollectors.remove(simulationRunId);
        if (collector != null) {
            collector.cancel(false);
        }
    }

    private void collectMetrics(BigInteger simulationRunId) {
        try {
            final var node = nodeService.getCurrentNode();
            Double cpuUsage = metricService.getCpuUsage();
            Double ramUsage = metricService.getRamUsage();

            SimulationMetric metric = SimulationMetric.builder()
                    .simulationRunId(simulationRunId)
                    .nodeId(node.getId())
                    .nodeName(node.getName())
                    .cpuUsage(cpuUsage)
                    .ramUsage(ramUsage)
                    .duration((int) collectionIntervalMs)
                    .build();

            simulationMetricRepository.save(metric);
        } catch (Exception e) {
            log.error("Error collecting metrics for simulation {}: {}", simulationRunId, e.getMessage());
        }
    }
}
