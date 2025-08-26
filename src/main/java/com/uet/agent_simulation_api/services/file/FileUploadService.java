package com.uet.agent_simulation_api.services.file;

import com.uet.agent_simulation_api.exceptions.errors.ProjectErrors;
import com.uet.agent_simulation_api.exceptions.project.ProjectNotFoundException;
import com.uet.agent_simulation_api.models.Experiment;
import com.uet.agent_simulation_api.models.Model;
import com.uet.agent_simulation_api.repositories.ExperimentRepository;
import com.uet.agent_simulation_api.repositories.ModelRepository;
import com.uet.agent_simulation_api.repositories.ProjectRepository;
import com.uet.agent_simulation_api.requests.model.UploadGamlRequest;
import com.uet.agent_simulation_api.responses.model.UploadGamlResponse;
import com.uet.agent_simulation_api.services.auth.IAuthService;
import com.uet.agent_simulation_api.services.validation.IGamaValidationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

/**
 * Service for handling file upload operations
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class FileUploadService implements IFileUploadService {
    
    private final IAuthService authService;
    private final IGamaValidationService gamaValidationService;
    private final ProjectRepository projectRepository;
    private final ModelRepository modelRepository;
    private final ExperimentRepository experimentRepository;
    
    @Value("${gama.path.project}")
    private String GAMA_PROJECT_ROOT_PATH;
    
    @Override
    @Transactional(rollbackFor = {Exception.class, Throwable.class})
    public UploadGamlResponse uploadAndValidateGamlFile(UploadGamlRequest request) {
        try {
            log.info("Starting GAML file upload for project: {}, experiment: {}", 
                    request.getProjectId(), request.getExperimentName());
            
            // Validate project exists
            var project = projectRepository.findById(request.getProjectId())
                    .orElseThrow(() -> new ProjectNotFoundException(ProjectErrors.E_PJ_0001.defaultMessage()));
            
            // Get current user
            var userId = authService.getCurrentUserId();
            
            // Prepare file paths
            var fileName = sanitizeFileName(request.getGamlFile().getOriginalFilename());
            var modelName = extractModelName(fileName);
            var projectPath = Paths.get(GAMA_PROJECT_ROOT_PATH, project.getLocation(), "models");
            var targetFilePath = projectPath.resolve(fileName);
            
            // Create directories if they don't exist
            Files.createDirectories(projectPath);
            
            // Save file directly to final location first
            saveFileToPath(request.getGamlFile(), targetFilePath);
            log.info("GAML file saved to: {}", targetFilePath);
            
            // Validate GAML file from final location
            var validationResult = gamaValidationService.validateGamlFile(targetFilePath);
            if (!validationResult.isValid()) {
                // Remove invalid file
                Files.deleteIfExists(targetFilePath);
                log.warn("GAML validation failed, file removed: {}", validationResult.message());
                return UploadGamlResponse.error("GAML file is not right format: " + validationResult.message());
            }
            
            // Save model to database
            var model = createModel(modelName, request.getProjectId(), userId);
            var savedModel = modelRepository.save(model);
            
            // Save experiment to database
            var experiment = createExperiment(request.getExperimentName(), savedModel.getId(), 
                                            request.getProjectId(), userId);
            var savedExperiment = experimentRepository.save(experiment);
            
            log.info("Successfully created model (ID: {}) and experiment (ID: {})", 
                    savedModel.getId(), savedExperiment.getId());
            
            return UploadGamlResponse.success(savedModel.getId(), savedExperiment.getId(), 
                                            modelName, request.getExperimentName());
            
        } catch (ProjectNotFoundException e) {
            log.error("Project not found: {}", request.getProjectId(), e);
            return UploadGamlResponse.error("Project not found");
        } catch (IOException e) {
            log.error("Error handling file upload", e);
            return UploadGamlResponse.error("Failed to process file: " + e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error during file upload", e);
            return UploadGamlResponse.error("Unexpected error occurred: " + e.getMessage());
        }
    }
    
    private void saveFileToPath(MultipartFile file, Path targetPath) throws IOException {
        try (var inputStream = file.getInputStream()) {
            Files.copy(inputStream, targetPath, StandardCopyOption.REPLACE_EXISTING);
        }
    }
    
    private String sanitizeFileName(String originalFileName) {
        if (originalFileName == null) {
            return "model.gaml";
        }
        
        // Remove any path separators and ensure .gaml extension
        var fileName = originalFileName.replaceAll("[/\\\\]", "");
        if (!fileName.toLowerCase().endsWith(".gaml")) {
            var nameWithoutExt = fileName.lastIndexOf('.') > 0 
                ? fileName.substring(0, fileName.lastIndexOf('.'))
                : fileName;
            fileName = nameWithoutExt + ".gaml";
        }
        
        return fileName;
    }
    
    private String extractModelName(String fileName) {
        // Remove .gaml extension to get model name
        return fileName.toLowerCase().endsWith(".gaml") 
            ? fileName.substring(0, fileName.length() - 5)
            : fileName;
    }
    
    private Model createModel(String modelName, java.math.BigInteger projectId, java.math.BigInteger userId) {
        return Model.builder()
                .name(modelName)
                .projectId(projectId)
                .userId(userId)
                .createdBy(authService.getCurrentUser().getEmail())
                .updatedBy(authService.getCurrentUser().getEmail())
                .build();
    }
    
    private Experiment createExperiment(String experimentName, java.math.BigInteger modelId, 
                                       java.math.BigInteger projectId, java.math.BigInteger userId) {
        return Experiment.builder()
                .name(experimentName)
                .modelId(modelId)
                .projectId(projectId)
                .userId(userId)
                .createdBy(authService.getCurrentUser().getEmail())
                .updatedBy(authService.getCurrentUser().getEmail())
                .build();
    }
}