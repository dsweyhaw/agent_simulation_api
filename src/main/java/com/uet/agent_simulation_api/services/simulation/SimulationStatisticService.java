package com.uet.agent_simulation_api.services.simulation;

import com.uet.agent_simulation_api.models.PigDataDaily;
import com.uet.agent_simulation_api.repositories.ExperimentResultRepository;
import com.uet.agent_simulation_api.repositories.PigDataDailyRepository;
import com.uet.agent_simulation_api.repositories.PigpenDailyRepository;
import com.uet.agent_simulation_api.responses.simulation.PigDailyResponse;
import com.uet.agent_simulation_api.responses.simulation.PigDataResponse;
import com.uet.agent_simulation_api.responses.simulation.PigpenDataResponse;
import com.uet.agent_simulation_api.responses.simulation.SimulationStatisticResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SimulationStatisticService implements ISimulationStatisticService{
    private final PigpenDailyRepository pigpenDailyRepository;
    private final PigDataDailyRepository pigDataDailyRepository;
    private final ExperimentResultRepository experimentResultRepository;

    @Override
    public SimulationStatisticResponse getSimulationStatistic(
        String experimentResultIds,
        String pigpenIds,
        String pigIds
    ) {
        if (experimentResultIds == null) {
            return null;
        }

        final var experimentResultIdList = Arrays.stream(experimentResultIds.split(","))
                .map(Integer::parseInt)
                .toList();
        final var simulationRunId = experimentResultRepository.getSimulationRunId(experimentResultIdList.getFirst());

        // Parse filter params
        Set<Integer> pigpenIdSet = pigpenIds != null ?
                Arrays.stream(pigpenIds.split(","))
                        .map(Integer::parseInt)
                        .collect(Collectors.toSet()) :
                null;

        Set<Integer> pigIdSet = pigIds != null ?
                Arrays.stream(pigIds.split(","))
                        .map(Integer::parseInt)
                        .collect(Collectors.toSet()) :
                null;

        // Get all pig data from pig_data_daily
        final var pigDataList = pigDataDailyRepository.findAllByRunId(BigInteger.valueOf(simulationRunId));

        // Filter by pigpen_ids and pig_ids if provided
        var filteredPigDataList = pigDataList.stream()
                .filter(data -> pigpenIdSet == null || pigpenIdSet.contains(data.getPigpenId()))
                .filter(data -> pigIdSet == null || pigIdSet.contains(data.getPigId()))
                .toList();

        // Group by pigpen_id
        Map<Integer, List<PigDataDaily>> pigpenGroups = filteredPigDataList.stream()
                .collect(Collectors.groupingBy(PigDataDaily::getPigpenId));

        List<PigpenDataResponse> pigpenResponses = new ArrayList<>();

        // Process each pigpen
        pigpenGroups.forEach((pigpenId, pigpenData) -> {
            // Group by pig_id within each pigpen
            Map<Integer, List<PigDataDaily>> pigGroups = pigpenData.stream()
                    .collect(Collectors.groupingBy(PigDataDaily::getPigId));

            List<PigDataResponse> pigDataResponses = new ArrayList<>();

            // Process each pig's data
            pigGroups.forEach((pigId, pigData) -> {
                List<PigDailyResponse> dailyResponses = pigData.stream()
                        .map(daily -> new PigDailyResponse(
                                daily.getDay(),
                                daily.getWeight(),
                                daily.getDfi(),
                                daily.getCfi(),
                                daily.getTargetCfi(),
                                daily.getTargetDfi(),
                                daily.getEatCount(),
                                daily.getExcreteCount(),
                                daily.getSeir()
                        ))
                        .sorted(Comparator.comparing(PigDailyResponse::day))
                        .collect(Collectors.toList());

                pigDataResponses.add(new PigDataResponse(pigId, dailyResponses));
            });

            pigpenResponses.add(new PigpenDataResponse(pigpenId, pigDataResponses));
        });

        return new SimulationStatisticResponse(pigpenResponses);
    }
}
