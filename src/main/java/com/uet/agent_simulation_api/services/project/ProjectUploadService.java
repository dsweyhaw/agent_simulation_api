package com.uet.agent_simulation_api.services.project;

import com.uet.agent_simulation_api.models.Experiment;
import com.uet.agent_simulation_api.models.Model;
import com.uet.agent_simulation_api.models.Project;
import com.uet.agent_simulation_api.repositories.ExperimentRepository;
import com.uet.agent_simulation_api.repositories.ModelRepository;
import com.uet.agent_simulation_api.repositories.ProjectRepository;
import com.uet.agent_simulation_api.requests.project.FinalizeProjectRequest;
import com.uet.agent_simulation_api.requests.project.UploadProjectRequest;
import com.uet.agent_simulation_api.responses.project.ProjectFinalizeResponse;
import com.uet.agent_simulation_api.responses.project.ProjectUploadResponse;
import com.uet.agent_simulation_api.services.auth.IAuthService;
import com.uet.agent_simulation_api.services.validation.IGamaValidationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Stream;

/**
 * Service for handling project upload operations
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ProjectUploadService implements IProjectUploadService {
    
    private final IAuthService authService;
    private final IGamaValidationService gamaValidationService;
    private final ProjectRepository projectRepository;
    private final ModelRepository modelRepository;
    private final ExperimentRepository experimentRepository;
    
    @Value("${gama.path.project}")
    private String GAMA_PROJECT_ROOT_PATH;
    
    private static final String TEMP_DIR_PREFIX = "/tmp/project_upload_";
    
    @Override
    public ProjectUploadResponse uploadProjectZip(UploadProjectRequest request) {
        try {
            log.info("Starting project ZIP upload: {}", request.getProjectName());
            
            // Create temporary directory
            String tempDirId = UUID.randomUUID().toString();
            Path tempDir = Paths.get(TEMP_DIR_PREFIX + tempDirId);
            Files.createDirectories(tempDir);
            
            // Extract ZIP file
            extractZipFile(request.getProjectZip().getInputStream(), tempDir);
            log.info("ZIP file extracted to: {}", tempDir);
            
            // Detect project name from extracted structure
            String detectedProjectName = detectProjectNameFromStructure(tempDir, request.getProjectName());
            log.info("Detected project name: {}", detectedProjectName);
            
            // Find and validate GAML files
            List<ProjectUploadResponse.GamlFileInfo> gamlFiles = findAndValidateGamlFiles(tempDir);
            log.info("Found {} GAML files", gamlFiles.size());
            
            var response = ProjectUploadResponse.success(tempDirId, gamlFiles);
            response.setDetectedProjectName(detectedProjectName);
            return response;
            
        } catch (IOException e) {
            log.error("Error during project ZIP upload", e);
            return ProjectUploadResponse.error("Failed to process ZIP file: " + e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error during project upload", e);
            return ProjectUploadResponse.error("Unexpected error occurred: " + e.getMessage());
        }
    }
    
    @Override
    @Transactional(rollbackFor = {Exception.class, Throwable.class})
    public ProjectFinalizeResponse finalizeProject(FinalizeProjectRequest request) {
        try {
            log.info("Finalizing project: {} with {} selected files", 
                    request.getProjectName(), request.getSelectedGamlFiles().size());
            
            Path tempDir = Paths.get(TEMP_DIR_PREFIX + request.getTempDirId());
            if (!Files.exists(tempDir)) {
                return ProjectFinalizeResponse.error("Temporary directory not found");
            }
            
            // Validate selected GAML files
            for (String gamlFile : request.getSelectedGamlFiles()) {
                Path gamlPath = tempDir.resolve(gamlFile);
                if (Files.exists(gamlPath)) {
                    var validationResult = gamaValidationService.validateGamlFile(gamlPath);
                    if (!validationResult.isValid()) {
                        return ProjectFinalizeResponse.error(
                            "GAML file '" + gamlFile + "' is invalid: " + validationResult.message());
                    }
                }
            }
            log.info("All selected GAML files validated successfully");
            
            // Create project in database with unique name handling
            var userId = authService.getCurrentUserId();
            log.info("Using user ID for project creation: {}", userId);
            var uniqueProjectName = generateUniqueProjectName(request.getProjectName(), userId);
            var projectLocation = "/" + sanitizeProjectName(uniqueProjectName);
            var project = createProject(uniqueProjectName, projectLocation, userId);
            var savedProject = projectRepository.save(project);
            
            // Create final project directory
            Path finalProjectDir = Paths.get(GAMA_PROJECT_ROOT_PATH, projectLocation);
            Files.createDirectories(finalProjectDir);
            
            // Copy entire project structure (excluding unselected GAML files)
            copyProjectStructure(tempDir, finalProjectDir, request.getSelectedGamlFiles());
            
            // Create models and experiments for selected GAML files
            List<BigInteger> modelIds = createModelsAndExperimentsForGamlFiles(
                request.getSelectedGamlFiles(), request.getExperimentNames(), savedProject.getId(), userId);
            
            // Clean up temporary directory
            cleanupTempDirectory(request.getTempDirId());
            
            log.info("Project finalized successfully: ID={}, Models={}", 
                    savedProject.getId(), modelIds.size());
            
            return ProjectFinalizeResponse.success(savedProject.getId(), request.getProjectName(), modelIds);
            
        } catch (IOException e) {
            log.error("Error finalizing project", e);
            return ProjectFinalizeResponse.error("Failed to finalize project: " + e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error finalizing project", e);
            return ProjectFinalizeResponse.error("Unexpected error occurred: " + e.getMessage());
        }
    }
    
    @Override
    public void cleanupTempDirectory(String tempDirId) {
        try {
            Path tempDir = Paths.get(TEMP_DIR_PREFIX + tempDirId);
            if (Files.exists(tempDir)) {
                deleteDirectoryRecursively(tempDir);
                log.info("Cleaned up temporary directory: {}", tempDir);
            }
        } catch (IOException e) {
            log.warn("Failed to cleanup temporary directory: {}", tempDirId, e);
        }
    }
    
    private void extractZipFile(java.io.InputStream zipInputStream, Path targetDir) throws IOException {
        // Read all bytes into memory first to work around ZipInputStream EXT descriptor issue
        byte[] zipData = zipInputStream.readAllBytes();
        
        // Create temporary file to use with ZipFile
        Path tempZipFile = Files.createTempFile("upload", ".zip");
        try {
            Files.write(tempZipFile, zipData);
            
            // Use ZipFile instead of ZipInputStream to handle EXT descriptors properly
            try (java.util.zip.ZipFile zipFile = new java.util.zip.ZipFile(tempZipFile.toFile())) {
                zipFile.stream().forEach(entry -> {
                    try {
                        // Skip invalid entries or __MACOSX folders
                        if (entry.getName().contains("__MACOSX") || entry.getName().startsWith("._")) {
                            return;
                        }
                        
                        Path entryPath = targetDir.resolve(entry.getName());
                        
                        // Security check: prevent zip slip
                        if (!entryPath.normalize().startsWith(targetDir.normalize())) {
                            log.warn("Skipping bad zip entry: {}", entry.getName());
                            return;
                        }
                        
                        if (entry.isDirectory()) {
                            Files.createDirectories(entryPath);
                        } else {
                            Files.createDirectories(entryPath.getParent());
                            try (java.io.InputStream entryInputStream = zipFile.getInputStream(entry)) {
                                Files.copy(entryInputStream, entryPath, StandardCopyOption.REPLACE_EXISTING);
                            }
                            log.debug("Extracted: {}", entryPath);
                        }
                    } catch (IOException e) {
                        log.warn("Failed to extract entry: {} - {}", entry.getName(), e.getMessage());
                    }
                });
            }
        } finally {
            // Clean up temporary ZIP file
            try {
                Files.deleteIfExists(tempZipFile);
            } catch (IOException e) {
                log.warn("Failed to delete temporary ZIP file: {}", tempZipFile, e);
            }
        }
    }
    
    private List<ProjectUploadResponse.GamlFileInfo> findAndValidateGamlFiles(Path directory) throws IOException {
        List<ProjectUploadResponse.GamlFileInfo> gamlFiles = new ArrayList<>();
        
        try (Stream<Path> paths = Files.walk(directory)) {
            paths.filter(Files::isRegularFile)
                 .filter(path -> path.toString().toLowerCase().endsWith(".gaml"))
                 .forEach(path -> {
                     try {
                         String fileName = path.getFileName().toString();
                         String relativePath = directory.relativize(path).toString();
                         long fileSize = Files.size(path);
                         
                         // Skip validation during upload - just list all GAML files as valid
                         // Validation will happen later during finalization for selected files only
                         gamlFiles.add(ProjectUploadResponse.GamlFileInfo.valid(fileName, relativePath, fileSize));
                         
                     } catch (IOException e) {
                         log.warn("Error processing GAML file: {}", path, e);
                     }
                 });
        }
        
        return gamlFiles;
    }
    
    private void copyProjectStructure(Path source, Path target, List<String> selectedGamlFiles) throws IOException {
        log.info("Copying project structure from {} to {}", source, target);
        
        // First, find the actual project content directory
        Path projectContentDir = findProjectContentDirectory(source);
        log.info("Detected project content directory: {}", projectContentDir);
        
        if (projectContentDir != null) {
            // Copy from the project content directory, flattening the structure
            copyProjectContent(projectContentDir, target, selectedGamlFiles, source);
        } else {
            // Fallback: copy directly from source (in case structure is already flat)
            copyProjectContent(source, target, selectedGamlFiles, source);
        }
    }
    
    private Path findProjectContentDirectory(Path tempDir) throws IOException {
        // Look for a subdirectory that contains 'models' and/or 'includes' folders
        try (Stream<Path> paths = Files.list(tempDir)) {
            var projectDirs = paths
                .filter(Files::isDirectory)
                .filter(dir -> {
                    try {
                        return Files.exists(dir.resolve("models")) || Files.exists(dir.resolve("includes"));
                    } catch (Exception e) {
                        return false;
                    }
                })
                .toList();
            
            if (!projectDirs.isEmpty()) {
                return projectDirs.get(0); // Return the first directory with project structure
            }
        }
        
        // Check if tempDir itself has the project structure (models/ and includes/ at root level)
        if (Files.exists(tempDir.resolve("models")) || Files.exists(tempDir.resolve("includes"))) {
            return tempDir;
        }
        
        return null;
    }
    
    private void copyProjectContent(Path contentDir, Path target, List<String> selectedGamlFiles, Path originalSource) throws IOException {
        Files.walkFileTree(contentDir, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                // Calculate relative path from the content directory (not original source)
                Path relativePath = contentDir.relativize(dir);
                Path targetDir = target.resolve(relativePath);
                Files.createDirectories(targetDir);
                return FileVisitResult.CONTINUE;
            }
            
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                // Calculate relative path from the content directory
                Path relativePath = contentDir.relativize(file);
                String relativePathStr = relativePath.toString();
                
                // For GAML files, need to check against the original selectedGamlFiles list
                // which might have the nested path structure
                if (file.toString().toLowerCase().endsWith(".gaml")) {
                    boolean shouldCopy = false;
                    
                    // Check if this file matches any of the selected GAML files
                    String fileName = file.getFileName().toString();
                    for (String selectedFile : selectedGamlFiles) {
                        if (selectedFile.endsWith(fileName) || selectedFile.equals(relativePathStr) || 
                            originalSource.relativize(file).toString().equals(selectedFile)) {
                            shouldCopy = true;
                            break;
                        }
                    }
                    
                    if (shouldCopy) {
                        Path targetFile = target.resolve(relativePath);
                        Files.copy(file, targetFile, StandardCopyOption.REPLACE_EXISTING);
                        log.debug("Copied GAML file: {} -> {}", file, targetFile);
                    }
                } else {
                    // Copy all non-GAML files (libraries, resources, etc.)
                    Path targetFile = target.resolve(relativePath);
                    Files.copy(file, targetFile, StandardCopyOption.REPLACE_EXISTING);
                    log.debug("Copied resource file: {} -> {}", file, targetFile);
                }
                
                return FileVisitResult.CONTINUE;
            }
        });
    }
    
    private List<BigInteger> createModelsAndExperimentsForGamlFiles(
            List<String> gamlFiles, 
            Map<String, String> experimentNames, 
            BigInteger projectId, 
            BigInteger userId) {
        List<BigInteger> modelIds = new ArrayList<>();
        
        for (String gamlFile : gamlFiles) {
            String modelName = extractModelName(gamlFile);
            String uniqueModelName = generateUniqueModelName(modelName, projectId, userId);
            var model = createModel(uniqueModelName, projectId, userId);
            var savedModel = modelRepository.save(model);
            modelIds.add(savedModel.getId());
            log.debug("Created model: {} (ID: {})", uniqueModelName, savedModel.getId());
            
            // Create experiment if experiment name is provided
            String experimentName = experimentNames != null ? experimentNames.get(gamlFile) : null;
            if (experimentName != null && !experimentName.trim().isEmpty()) {
                var experiment = createExperiment(experimentName.trim(), savedModel.getId(), projectId, userId);
                var savedExperiment = experimentRepository.save(experiment);
                log.debug("Created experiment: {} (ID: {}) for model: {}", 
                    experimentName, savedExperiment.getId(), uniqueModelName);
            }
        }
        
        return modelIds;
    }
    
    private void deleteDirectoryRecursively(Path directory) throws IOException {
        Files.walkFileTree(directory, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Files.delete(file);
                return FileVisitResult.CONTINUE;
            }
            
            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                Files.delete(dir);
                return FileVisitResult.CONTINUE;
            }
        });
    }
    
    private String detectProjectNameFromStructure(Path tempDir, String fallbackName) throws IOException {
        // Look for the main project directory (should contain both 'includes' and 'models' folders)
        try (Stream<Path> paths = Files.list(tempDir)) {
            var projectDirs = paths
                .filter(Files::isDirectory)
                .filter(dir -> {
                    try {
                        return Files.exists(dir.resolve("models")) || Files.exists(dir.resolve("includes"));
                    } catch (Exception e) {
                        return false;
                    }
                })
                .toList();
            
            if (!projectDirs.isEmpty()) {
                // Use the first directory that contains models or includes
                return projectDirs.get(0).getFileName().toString();
            }
        }
        
        // If no clear project structure, use fallback name
        return sanitizeProjectName(fallbackName);
    }
    
    private String sanitizeProjectName(String projectName) {
        return projectName.toLowerCase()
                         .replaceAll("[^a-z0-9\\-_]", "-")
                         .replaceAll("-+", "-")
                         .trim();
    }
    
    /**
     * Generate a unique project name by checking existing projects and adding suffix if needed
     * @param requestedName The originally requested project name
     * @param userId The user ID who is creating the project
     * @return A unique project name (e.g., "Tsunami", "Tsunami-1", "Tsunami-2", etc.)
     */
    private String generateUniqueProjectName(String requestedName, BigInteger userId) {
        String baseName = requestedName.trim();
        String uniqueName = baseName;
        int counter = 1;
        
        // Check if the name already exists for this user
        while (projectRepository.existsByNameAndUserId(uniqueName, userId)) {
            uniqueName = baseName + "-" + counter;
            counter++;
            
            // Safety check to prevent infinite loop (max 1000 duplicates)
            if (counter > 1000) {
                uniqueName = baseName + "-" + System.currentTimeMillis();
                break;
            }
        }
        
        if (!uniqueName.equals(baseName)) {
            log.info("Project name '{}' already exists, using unique name: '{}'", baseName, uniqueName);
        }
        
        return uniqueName;
    }
    
    private String extractModelName(String gamlFilePath) {
        Path path = Paths.get(gamlFilePath);
        String fileName = path.getFileName().toString();
        return fileName.toLowerCase().endsWith(".gaml") 
            ? fileName.substring(0, fileName.length() - 5)
            : fileName;
    }
    
    private Project createProject(String projectName, String location, BigInteger userId) {
        return Project.builder()
                .name(projectName)
                .location(location)
                .userId(userId)
                .createdBy("admin@uet.vn")  // Hardcoded for development
                .updatedBy("admin@uet.vn")  // Hardcoded for development
                .build();
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
    
    private Experiment createExperiment(String experimentName, BigInteger modelId, BigInteger projectId, BigInteger userId) {
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