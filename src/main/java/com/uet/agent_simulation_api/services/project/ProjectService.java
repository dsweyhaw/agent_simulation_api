package com.uet.agent_simulation_api.services.project;

import com.uet.agent_simulation_api.models.Project;
import com.uet.agent_simulation_api.repositories.ProjectRepository;
import com.uet.agent_simulation_api.repositories.ModelRepository;
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
public class ProjectService implements IProjectService {
    private final IAuthService authService;
    private final ProjectRepository projectRepository;
    private final ModelRepository modelRepository;
    private final ExperimentRepository experimentRepository;
    private final ExperimentResultRepository experimentResultRepository;
    
    @Value("${app.project.storage.path:/app/projects}")
    private String projectStoragePath;

    @Override
    public List<Project> get() {
        return projectRepository.find(authService.getCurrentUserId());
    }

    @Override
    public Optional<Project> getProject(BigInteger id) {
        return projectRepository.findById(id);
    }
    
    @Override
    @Transactional
    public boolean deleteProject(BigInteger projectId) {
        try {
            BigInteger userId = authService.getCurrentUserId();
            
            // First, get the project to get its location for file system cleanup
            Optional<Project> projectOpt = projectRepository.findById(projectId);
            if (projectOpt.isEmpty()) {
                log.warn("Project not found with id: {}", projectId);
                return false;
            }
            
            Project project = projectOpt.get();
            
            // Security check - make sure the project belongs to the current user
            if (!project.getUserId().equals(userId)) {
                log.warn("User {} attempted to delete project {} owned by user {}", 
                         userId, projectId, project.getUserId());
                return false;
            }
            
            // Delete related records in the correct order to avoid foreign key constraint violations
            // 1. First delete experiment results that reference experiments in this project
            int experimentResultsDeleted = experimentResultRepository.deleteAllByProjectIdAndUserId(projectId, userId);
            log.info("Deleted {} experiment results for project {}", experimentResultsDeleted, projectId);
            
            // 2. Then delete experiments that reference models in this project
            int experimentsDeleted = experimentRepository.deleteAllByProjectIdAndUserId(projectId, userId);
            log.info("Deleted {} experiments for project {}", experimentsDeleted, projectId);
            
            // 3. Then delete all models associated with this project
            int modelsDeleted = modelRepository.deleteAllByProjectIdAndUserId(projectId, userId);
            log.info("Deleted {} models for project {}", modelsDeleted, projectId);
            
            // 4. Finally delete the project from database
            int projectsDeleted = projectRepository.deleteByIdAndUserId(projectId, userId);
            
            if (projectsDeleted > 0) {
                // Delete project directory from file system
                deleteProjectDirectory(project.getLocation());
                log.info("Successfully deleted project {} with {} models, {} experiments, and {} experiment results", 
                         projectId, modelsDeleted, experimentsDeleted, experimentResultsDeleted);
                return true;
            } else {
                log.warn("No project deleted for id: {} and user: {}", projectId, userId);
                return false;
            }
            
        } catch (Exception e) {
            log.error("Error deleting project {}: {}", projectId, e.getMessage(), e);
            return false;
        }
    }
    
    private void deleteProjectDirectory(String projectLocation) {
        try {
            Path projectPath = Paths.get(projectStoragePath, projectLocation);
            if (Files.exists(projectPath)) {
                deleteDirectoryRecursively(projectPath);
                log.info("Deleted project directory: {}", projectPath);
            } else {
                log.warn("Project directory not found: {}", projectPath);
            }
        } catch (IOException e) {
            log.error("Error deleting project directory {}: {}", projectLocation, e.getMessage(), e);
        }
    }
    
    private void deleteDirectoryRecursively(Path path) throws IOException {
        if (Files.isDirectory(path)) {
            Files.walk(path)
                .sorted((path1, path2) -> path2.compareTo(path1)) // Delete files before directories
                .forEach(p -> {
                    try {
                        Files.delete(p);
                    } catch (IOException e) {
                        log.error("Failed to delete file/directory: {}", p, e);
                    }
                });
        } else {
            Files.deleteIfExists(path);
        }
    }
}
