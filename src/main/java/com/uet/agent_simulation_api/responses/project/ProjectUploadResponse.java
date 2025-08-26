package com.uet.agent_simulation_api.responses.project;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * Response DTO for project upload
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProjectUploadResponse {
    private boolean success;
    private String message;
    private String tempDirId;
    private String detectedProjectName;
    private List<GamlFileInfo> gamlFiles;
    
    public static ProjectUploadResponse success(String tempDirId, List<GamlFileInfo> gamlFiles) {
        return new ProjectUploadResponse(true, "Project uploaded successfully", tempDirId, null, gamlFiles);
    }
    
    public static ProjectUploadResponse error(String message) {
        return new ProjectUploadResponse(false, message, null, null, null);
    }
    
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GamlFileInfo {
        private String fileName;
        private String relativePath;
        private long fileSize;
        private boolean isValid;
        private String validationError;
        private String experimentName; // Optional experiment name for this GAML file
        
        public static GamlFileInfo valid(String fileName, String relativePath, long fileSize) {
            return new GamlFileInfo(fileName, relativePath, fileSize, true, null, null);
        }
        
        public static GamlFileInfo invalid(String fileName, String relativePath, long fileSize, String error) {
            return new GamlFileInfo(fileName, relativePath, fileSize, false, error, null);
        }
    }
}