package com.uet.agent_simulation_api.requests.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigInteger;

/**
 * Request DTO for uploading GAML files
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UploadGamlRequest {
    @NotNull(message = "Project ID is required")
    private BigInteger projectId;
    
    @NotBlank(message = "Experiment name is required")
    private String experimentName;
    
    @NotNull(message = "GAML file is required")
    private MultipartFile gamlFile;
}