package com.uet.agent_simulation_api.controllers;

import com.uet.agent_simulation_api.requests.model.UploadGamlRequest;
import com.uet.agent_simulation_api.responses.ResponseHandler;
import com.uet.agent_simulation_api.responses.SuccessResponse;
import com.uet.agent_simulation_api.services.file.IFileUploadService;
import com.uet.agent_simulation_api.services.model.IModelService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigInteger;

@RestController
@RequestMapping("/api/v1/models")
@RequiredArgsConstructor
public class ModelController {
    private final IModelService modelService;
    private final IFileUploadService fileUploadService;
    private final ResponseHandler responseHandler;

    @GetMapping
    public ResponseEntity<SuccessResponse> get(@RequestParam(name = "project_id", required = false) BigInteger projectId,
            @RequestParam(name = "has_experiment", required = false) Boolean hasExperiment) {
        return responseHandler.respondSuccess(modelService.get(projectId, hasExperiment));
    }
    
    @PostMapping("/upload")
    public ResponseEntity<?> uploadGamlFile(@Valid @ModelAttribute UploadGamlRequest request) {
        var result = fileUploadService.uploadAndValidateGamlFile(request);
        
        if (result.isSuccess()) {
            return responseHandler.respondSuccess(result);
        } else {
            var errorDetails = new com.uet.agent_simulation_api.exceptions.errors.ErrorDetails(
                HttpStatus.BAD_REQUEST,
                "GAML_VALIDATION_ERROR",
                result.getMessage()
            );
            return responseHandler.respondError(
                new RuntimeException(result.getMessage()), 
                errorDetails
            );
        }
    }
}
