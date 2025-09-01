package com.uet.agent_simulation_api.controllers;

import com.uet.agent_simulation_api.requests.project.FinalizeProjectRequest;
import com.uet.agent_simulation_api.requests.project.UploadProjectRequest;
import com.uet.agent_simulation_api.responses.ResponseHandler;
import com.uet.agent_simulation_api.responses.SuccessResponse;
import com.uet.agent_simulation_api.services.project.IProjectService;
import com.uet.agent_simulation_api.services.project.IProjectUploadService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.math.BigInteger;

@RestController
@RequestMapping("/api/v1/projects")
@RequiredArgsConstructor
@Slf4j
public class ProjectController {
    private final ResponseHandler responseHandler;
    private final IProjectService projectService;
    private final IProjectUploadService projectUploadService;

    @GetMapping
    public ResponseEntity<SuccessResponse> get() {
        return responseHandler.respondSuccess(projectService.get());
    }
    
    @PostMapping("/upload")
    public ResponseEntity<?> uploadProject(@Valid @ModelAttribute UploadProjectRequest request) {
        var result = projectUploadService.uploadProjectZip(request);
        
        if (result.isSuccess()) {
            return responseHandler.respondSuccess(result);
        } else {
            var errorDetails = new com.uet.agent_simulation_api.exceptions.errors.ErrorDetails(
                HttpStatus.BAD_REQUEST,
                "PROJECT_UPLOAD_ERROR",
                result.getMessage()
            );
            return responseHandler.respondError(
                new RuntimeException(result.getMessage()), 
                errorDetails
            );
        }
    }
    
    @PostMapping("/finalize")
    public ResponseEntity<?> finalizeProject(@Valid @RequestBody FinalizeProjectRequest request) {
        var result = projectUploadService.finalizeProject(request);
        
        if (result.isSuccess()) {
            return responseHandler.respondSuccess(result);
        } else {
            var errorDetails = new com.uet.agent_simulation_api.exceptions.errors.ErrorDetails(
                HttpStatus.BAD_REQUEST,
                "PROJECT_FINALIZE_ERROR",
                result.getMessage()
            );
            return responseHandler.respondError(
                new RuntimeException(result.getMessage()), 
                errorDetails
            );
        }
    }
    
    @DeleteMapping("/temp/{tempDirId}")
    public ResponseEntity<SuccessResponse> cleanupTempDirectory(@PathVariable String tempDirId) {
        projectUploadService.cleanupTempDirectory(tempDirId);
        return responseHandler.respondSuccess("Temporary directory cleaned up successfully");
    }
    
    @DeleteMapping("/{projectId}")
    public ResponseEntity<SuccessResponse> deleteProject(@PathVariable BigInteger projectId) {
        boolean deleted = projectService.deleteProject(projectId);
        
        if (deleted) {
            return responseHandler.respondSuccess("Project deleted successfully");
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
