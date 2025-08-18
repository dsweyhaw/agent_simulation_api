package com.uet.agent_simulation_api.responses.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigInteger;

/**
 * Response DTO for GAML file upload
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UploadGamlResponse {
    private BigInteger modelId;
    private BigInteger experimentId;
    private String modelName;
    private String experimentName;
    private String message;
    private boolean success;
    
    public static UploadGamlResponse success(BigInteger modelId, BigInteger experimentId, String modelName, String experimentName) {
        return new UploadGamlResponse(modelId, experimentId, modelName, experimentName, "GAML file uploaded and validated successfully", true);
    }
    
    public static UploadGamlResponse error(String message) {
        return new UploadGamlResponse(null, null, null, null, message, false);
    }
}