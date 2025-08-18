package com.uet.agent_simulation_api.controllers;

import com.uet.agent_simulation_api.responses.ResponseHandler;
import com.uet.agent_simulation_api.responses.SuccessResponse;
import com.uet.agent_simulation_api.services.simulation.ISimulationStatisticService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/api/v1/simulation_statistics")
@RequiredArgsConstructor
public class SimulationStatisticController {
    private final ResponseHandler responseHandler;
    private final ISimulationStatisticService simulationStatisticService;

    /**
     * Get simulation statistics.
     *
     * @return ResponseEntity<SuccessResponse>
     */
    @GetMapping
    public ResponseEntity<SuccessResponse> get(
            @RequestParam(name = "experiment_result_ids") String experimentResultIds,
            @RequestParam(name = "pigpen_ids", required = false) String pigpenIds,
            @RequestParam(name = "pig_ids", required = false) String pigIds
    ) {
        final var result = simulationStatisticService.getSimulationStatistic(
                experimentResultIds,
                pigpenIds,
                pigIds
        );

        return responseHandler.respondSuccess(result);
    }
}
