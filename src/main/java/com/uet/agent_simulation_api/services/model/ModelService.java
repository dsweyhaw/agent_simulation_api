package com.uet.agent_simulation_api.services.model;

import com.uet.agent_simulation_api.exceptions.errors.ModelErrors;
import com.uet.agent_simulation_api.exceptions.model.ModelNotFoundException;
import com.uet.agent_simulation_api.models.Model;
import com.uet.agent_simulation_api.models.Project;
import com.uet.agent_simulation_api.repositories.ModelRepository;
import com.uet.agent_simulation_api.repositories.ProjectRepository;
import com.uet.agent_simulation_api.repositories.ExperimentRepository;
import com.uet.agent_simulation_api.repositories.ExperimentResultRepository;
import com.uet.agent_simulation_api.services.auth.IAuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ModelService implements IModelService {
    private final IAuthService authService;
    private final ModelRepository modelRepository;
    private final ProjectRepository projectRepository;
    private final ExperimentRepository experimentRepository;
    private final ExperimentResultRepository experimentResultRepository;
    
    @Value("${app.project.storage.path:/app/projects}")
    private String projectStoragePath;

    @Override
    public List<Model> get(BigInteger projectId, Boolean hasExperiment) {
        if (hasExperiment != null) {
            return modelRepository.findByExperimentNotNull(authService.getCurrentUserId(), projectId);
        }

        return modelRepository.find(authService.getCurrentUserId(), projectId);
    }

    @Override
    public Model getModel(BigInteger modelId) {
        return modelRepository.findById(modelId).orElseThrow(
            () -> new ModelNotFoundException(ModelErrors.E_MODEL_0001.defaultMessage())
        );
    }
    
    @Override
    @Transactional
    public boolean deleteModel(BigInteger modelId, BigInteger projectId) {
        try {
            BigInteger userId = authService.getCurrentUserId();
            
            // First, get the model to validate and get its information for file system cleanup
            Optional<Model> modelOpt = modelRepository.findById(modelId);
            if (modelOpt.isEmpty()) {
                log.warn("Model not found with id: {}", modelId);
                return false;
            }
            
            Model model = modelOpt.get();
            
            // Security check - make sure the model belongs to the current user and project
            if (!model.getUserId().equals(userId) || !model.getProjectId().equals(projectId)) {
                log.warn("User {} attempted to delete model {} from project {} but model belongs to user {} and project {}", 
                         userId, modelId, projectId, model.getUserId(), model.getProjectId());
                return false;
            }
            
            // Get project information for file system cleanup
            Optional<Project> projectOpt = projectRepository.findById(projectId);
            if (projectOpt.isEmpty()) {
                log.warn("Project not found with id: {}", projectId);
                return false;
            }
            
            Project project = projectOpt.get();
            
            // Delete related records in the correct order to avoid foreign key constraint violations
            // 1. First delete experiment results that reference experiments of this model
            int experimentResultsDeleted = experimentResultRepository.deleteAllByModelIdAndUserId(modelId, userId);
            log.info("Deleted {} experiment results for model {}", experimentResultsDeleted, modelId);
            
            // 2. Then delete experiments that reference this model
            int experimentsDeleted = experimentRepository.deleteAllByModelIdAndUserId(modelId, userId);
            log.info("Deleted {} experiments for model {}", experimentsDeleted, modelId);
            
            // 3. Finally delete the model
            int modelsDeleted = modelRepository.deleteByIdAndProjectIdAndUserId(modelId, projectId, userId);
            
            if (modelsDeleted > 0) {
                // Delete model file from file system
                deleteModelFile(project.getLocation(), model.getName());
                log.info("Successfully deleted model {} from project {}", modelId, projectId);
                return true;
            } else {
                log.warn("No model deleted for id: {}, project: {} and user: {}", modelId, projectId, userId);
                return false;
            }
            
        } catch (Exception e) {
            log.error("Error deleting model {} from project {}: {}", modelId, projectId, e.getMessage(), e);
            return false;
        }
    }
    
    private void deleteModelFile(String projectLocation, String modelName) {
        try {
            // Construct the model file path
            String gamlFileName = modelName.endsWith(".gaml") ? modelName : modelName + ".gaml";
            Path modelPath = Paths.get(projectStoragePath, projectLocation, "models", gamlFileName);
            
            if (Files.exists(modelPath)) {
                Files.delete(modelPath);
                log.info("Deleted model file: {}", modelPath);
            } else {
                log.warn("Model file not found: {}", modelPath);
            }
        } catch (IOException e) {
            log.error("Error deleting model file {} from project {}: {}", modelName, projectLocation, e.getMessage(), e);
        }
    }
}
