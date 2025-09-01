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
import java.math.BigInteger;
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
                    request.getProjectId(), 
                    request.getExperimentName() != null ? request.getExperimentName() : "none");
            
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
            
            // Generate unique model name to handle duplicates
            var uniqueModelName = generateUniqueModelName(modelName, request.getProjectId(), userId);
            
            // Save model to database
            var model = createModel(uniqueModelName, request.getProjectId(), userId);
            var savedModel = modelRepository.save(model);
            
            // Save experiment to database only if experiment name is provided
            BigInteger experimentId = null;
            String experimentName = request.getExperimentName();
            
            if (experimentName != null && !experimentName.trim().isEmpty()) {
                var experiment = createExperiment(experimentName.trim(), savedModel.getId(), 
                                                request.getProjectId(), userId);
                var savedExperiment = experimentRepository.save(experiment);
                experimentId = savedExperiment.getId();
                log.info("Successfully created model (ID: {}) and experiment (ID: {})", 
                        savedModel.getId(), experimentId);
            } else {
                log.info("Successfully created model (ID: {}) without experiment", 
                        savedModel.getId());
            }
            
            return UploadGamlResponse.success(savedModel.getId(), experimentId, 
                                            uniqueModelName, experimentName);
            
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
    
    private Model createModel(String modelName, BigInteger projectId, BigInteger userId) {
        return Model.builder()
                .name(modelName)
                .projectId(projectId)
                .userId(userId)
                .createdBy("admin@uet.vn")  // Hardcoded for development
                .updatedBy("admin@uet.vn")  // Hardcoded for development
                .build();
    }
    
    private Experiment createExperiment(String experimentName, BigInteger modelId, 
                                       BigInteger projectId, BigInteger userId) {
        return Experiment.builder()
                .name(experimentName)
                .modelId(modelId)
                .projectId(projectId)
                .userId(userId)
                .createdBy("admin@uet.vn")  // Hardcoded for development
                .updatedBy("admin@uet.vn")  // Hardcoded for development
                .build();
    }
    
    /**
     * Generate a unique model name by checking existing models in the project and adding suffix if needed
     * @param requestedName The originally requested model name
     * @param projectId The project ID where the model will be created
     * @param userId The user ID who is creating the model
     * @return A unique model name (e.g., "my-model", "my-model-1", "my-model-2", etc.)
     */
    private String generateUniqueModelName(String requestedName, BigInteger projectId, BigInteger userId) {
        String baseName = requestedName.trim();
        String uniqueName = baseName;
        int counter = 1;
        
        // Check if the name already exists in this project for this user
        while (modelRepository.existsByNameAndProjectIdAndUserId(uniqueName, projectId, userId)) {
            uniqueName = baseName + "-" + counter;
            counter++;
            
            // Safety check to prevent infinite loop (max 1000 duplicates)
            if (counter > 1000) {
                uniqueName = baseName + "-" + System.currentTimeMillis();
                break;
            }
        }
        
        if (!uniqueName.equals(baseName)) {
            log.info("Model name '{}' already exists in project {}, using unique name: '{}'", 
                    baseName, projectId, uniqueName);
        }
        
        return uniqueName;
    }
}