package com.uet.agent_simulation_api.controllers;

import com.uet.agent_simulation_api.responses.ResponseHandler;
import com.uet.agent_simulation_api.responses.SuccessResponse;
import com.uet.agent_simulation_api.services.experiment.ExperimentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigInteger;

@RestController
@RequestMapping("/api/v1/experiments")
@RequiredArgsConstructor
public class ExperimentController {
    private final ResponseHandler responseHandler;
    private final ExperimentService experimentService;

    /**
     * Get experiments.
     *
     * @param projectId BigInteger
     * @param modelId BigInteger
     *
     * @return ResponseEntity<SuccessResponse>
     */
    @GetMapping
    public ResponseEntity<SuccessResponse> get(@RequestParam(name = "project_id", required = false) BigInteger projectId,
        @RequestParam(name = "model_id", required = false) BigInteger modelId) {
        return responseHandler.respondSuccess(experimentService.get(projectId, modelId));
    }
}
