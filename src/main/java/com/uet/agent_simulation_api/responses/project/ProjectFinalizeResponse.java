package com.uet.agent_simulation_api.responses.project;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigInteger;
import java.util.List;

/**
 * Response DTO for project finalization
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProjectFinalizeResponse {
    private boolean success;
    private String message;
    private BigInteger projectId;
    private String projectName;
    private List<BigInteger> modelIds;
    
    public static ProjectFinalizeResponse success(BigInteger projectId, String projectName, List<BigInteger> modelIds) {
        return new ProjectFinalizeResponse(true, "Project created successfully", projectId, projectName, modelIds);
    }
    
    public static ProjectFinalizeResponse error(String message) {
        return new ProjectFinalizeResponse(false, message, null, null, null);
    }
}