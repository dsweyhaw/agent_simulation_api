package com.uet.agent_simulation_api.controllers;

import com.uet.agent_simulation_api.responses.ResponseHandler;
import com.uet.agent_simulation_api.responses.SuccessResponse;
import com.uet.agent_simulation_api.services.experiment_result.IExperimentResultService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigInteger;

@RestController
@RequestMapping("/api/v1/experiment_results")
@RequiredArgsConstructor
public class ExperimentResultController {
    private final ResponseHandler responseHandler;
    private final IExperimentResultService experimentResultService;

    /**
     * Get experiment results.
     *
     * @param experimentId BigInteger
     * @param modelId BigInteger
     * @param projectId BigInteger
     *
     * @return ResponseEntity<SuccessResponse>
     */
    @GetMapping
    public ResponseEntity<SuccessResponse> get(
            @RequestParam(name = "experiment_id", required = false) BigInteger experimentId,
            @RequestParam(name = "model_id", required = false) BigInteger modelId,
            @RequestParam(name = "project_id", required = false) BigInteger projectId,
            @RequestParam(name = "node_id", required = false) Integer nodeId) {
        return responseHandler.respondSuccess(experimentResultService.get(experimentId, modelId, projectId, nodeId));
    }

    /**
     * Get experiment result details.
     *
     * @param id BigInteger
     *
     * @return ResponseEntity<SuccessResponse>
     */
    @GetMapping("/{id}")
    public ResponseEntity<SuccessResponse> recreate(@PathVariable BigInteger id) {
        return responseHandler.respondSuccess(experimentResultService.getDetails(id));
    }

    /**
     * Check process.
     *
     * @return ResponseEntity<SuccessResponse>
     */
    @GetMapping("/{id}/progress")
    public ResponseEntity<SuccessResponse> getExperimentProgress(@PathVariable BigInteger id) {
        return responseHandler.respondSuccess(experimentResultService.getExperimentProgress(id));
    }

    /**
     * Download experiment result.
     *
     * @param id BigInteger
     *
     * @return ResponseEntity<InputStreamResource>
     */
    @GetMapping("/{id}/download")
    public ResponseEntity<InputStreamResource> download(@PathVariable BigInteger id) {
        final var result = experimentResultService.download(id);

        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + result.filename())
            .contentType(MediaType.APPLICATION_OCTET_STREAM)
            .contentLength(result.fileSize())
            .body(result.resource());
    }

    @DeleteMapping("/{id}/stop")
    public ResponseEntity<SuccessResponse> stop(@PathVariable BigInteger id) {
        experimentResultService.stop(id);

        return responseHandler.respondSuccess("OK");
    }
}
