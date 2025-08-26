package com.uet.agent_simulation_api.requests.project;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Map;

/**
 * Request DTO for finalizing project with selected GAML files
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FinalizeProjectRequest {
    @NotBlank(message = "Project name is required")
    private String projectName;
    
    @NotBlank(message = "Temporary directory ID is required")
    private String tempDirId;
    
    @NotEmpty(message = "At least one GAML file must be selected")
    private List<String> selectedGamlFiles;
    
    // Map of gaml file path -> experiment name (optional)
    private Map<String, String> experimentNames;
}