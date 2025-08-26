package com.uet.agent_simulation_api.requests.project;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

/**
 * Request DTO for uploading project ZIP files
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UploadProjectRequest {
    @NotBlank(message = "Project name is required")
    private String projectName;
    
    @NotNull(message = "Project ZIP file is required")
    private MultipartFile projectZip;
}