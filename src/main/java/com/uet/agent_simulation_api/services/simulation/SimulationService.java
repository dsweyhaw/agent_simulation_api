package com.uet.agent_simulation_api.services.simulation;

import com.uet.agent_simulation_api.commands.builders.IGamaCommandBuilder;
import com.uet.agent_simulation_api.commands.executors.IGamaCommandExecutor;
import com.uet.agent_simulation_api.constant.ExperimentResultStatusConst;
import com.uet.agent_simulation_api.constant.SimulationConst;
import com.uet.agent_simulation_api.exceptions.errors.ProjectErrors;
import com.uet.agent_simulation_api.exceptions.simulation.CannotClearOldSimulationOutputException;
import com.uet.agent_simulation_api.exceptions.project.ProjectNotFoundException;
import com.uet.agent_simulation_api.models.Experiment;
import com.uet.agent_simulation_api.models.ExperimentResult;
import com.uet.agent_simulation_api.models.ExperimentResultCategory;
import com.uet.agent_simulation_api.models.ExperimentResultImage;
import com.uet.agent_simulation_api.repositories.ExperimentResultCategoryRepository;
import com.uet.agent_simulation_api.repositories.ExperimentResultImageRepository;
import com.uet.agent_simulation_api.repositories.ProjectRepository;
import com.uet.agent_simulation_api.requests.simulation.CreateSimulationRequest;
import com.uet.agent_simulation_api.services.auth.IAuthService;
import com.uet.agent_simulation_api.services.experiment_result.IExperimentResultService;
import com.uet.agent_simulation_api.services.image.IImageService;
import com.uet.agent_simulation_api.services.metric.MetricsCollectionService;
import com.uet.agent_simulation_api.services.node.INodeService;
import com.uet.agent_simulation_api.utils.ConvertUtil;
import com.uet.agent_simulation_api.utils.FileUtil;
import com.uet.agent_simulation_api.utils.TimeUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

/**
 * This service is used to handle simulation logic.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SimulationService implements ISimulationService {
    private final TimeUtil timeUtil;
    private final FileUtil fileUtil;
//    private final IS3Service s3Service;
    private final ConvertUtil convertUtil;
    private final IAuthService authService;
    private final INodeService nodeService;
    private final IImageService imageService;
    private final ProjectRepository projectRepository;
    private final ExecutorService virtualThreadExecutor;
    private final IGamaCommandBuilder gamaCommandBuilder;
    private final IGamaCommandExecutor gamaCommandExecutor;
    private final IExperimentResultService experimentResultService;
    private final ExperimentResultImageRepository experimentResultImageRepository;
    private final ExperimentResultCategoryRepository experimentResultCategoryRepository;
    private final MetricsCollectionService metricsCollectionService;

    @Value("${gama.path.project}")
    private String GAMA_PROJECT_ROOT_PATH;

    @Value("${gama.path.output}")
    private String GAMA_OUTPUT_DIR;

    @Value("${gama.path.xml}")
    private String GAMA_XML_FILE_PATH;

    @Override
    public void run() {
        final var start = timeUtil.getCurrentTime();
        log.info("Simulation started at {}", timeUtil.getCurrentTimeString());

        final var processBuilder = new ProcessBuilder();

        final var command = gamaCommandBuilder.buildLegacy(
                Map.of(
                        "-hpc", "8",
                        "-m", "8"
                ),
                "/Users/trungdt/Workspaces/uet/gama/pig-farm/models/sml-01.xml",
                "/Users/trungdt/Workspaces/uet/gama/pig-farm/models/test_output/"
        );
        /*
        final String batchCommand = gamaCommandBuilder.buildBatch(
                Map.of(
                        "-hpc", "2",
                        "-m", "8"
                ),
                "Normal",
                "/Users/trungdt/Workspaces/uet/gama/pig-farm/models/simulator-01.gaml"
        );
        */

        try {
            log.info("Clear old output directory");
            processBuilder.command("bash", "-c", "rm -rf /Users/trungdt/Workspaces/uet/gama/pig-farm/includes/output/normal/**").start();

            log.info("Running command: {}", command);
            final var process = processBuilder.command("bash", "-c", command).start();

            final var reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                log.info(line);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        log.info("Simulation ended at {}, took {} seconds",
                timeUtil.getCurrentTimeString(),
                (timeUtil.getCurrentTime() - start) * 1.0 / 1000
        );
    }

    @Override
//    @Transactional(rollbackFor = { Exception.class, Throwable.class })
    public void run(CreateSimulationRequest request) {
        // Run simulation
        runSimulation(request);
    }

    /**
     * This method is used to run simulation.
     *
     * @param request CreateSimulationRequest
     */
    private void runSimulation(CreateSimulationRequest request) {
        final var nodeId = nodeService.getCurrentNodeId() != null ? nodeService.getCurrentNodeId().toString() : "";

        final var userId = authService.getCurrentUserId();
        final var projectId = request.getProjectId();
        final var project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ProjectNotFoundException(ProjectErrors.E_PJ_0001.defaultMessage()));

        final var projectLocation = GAMA_PROJECT_ROOT_PATH + project.getLocation();
        final var gamaParams = request.getGamaParams();

        request.getExperiments().forEach(experimentReq -> {
            final var experimentResultNumber = experimentReq.getExperimentResultNumber();
            // Get experiment data from request.
            final var modelId = experimentReq.getModelId();
            final var experimentId = experimentReq.getId();
            final var gamlFile = experimentReq.getGamlFile();
            final var finalStep = experimentReq.getFinalStep() != null ? experimentReq.getFinalStep() : SimulationConst.DEFAULT_FINAL_STEP;
            final var gamlFileName = gamlFile.replace(".gaml", "");
            final var experimentName = experimentReq.getExperiment().getName();
            final var pathToExperimentPlanXmlFile = getPathToExperimentPlanXmlFile(nodeId, userId, projectId, modelId,
                    experimentId, experimentResultNumber, gamlFileName, experimentName);
            final var pathToLocalExperimentOutputDir = getPathToLocalExperimentOutputDir(nodeId, userId, projectId,
                    modelId, experimentId, experimentResultNumber, gamlFileName, experimentName);

            // Clear old xml file
            clearLocalResource(pathToExperimentPlanXmlFile);

            // Create experiment plan XML file.
            final var createXmlCommand = gamaCommandBuilder.createXmlFile(
                    experimentName,  projectLocation + "/models/" + gamlFile, pathToExperimentPlanXmlFile);

            // Build command to run experiment.
            final var runLegacyCommand = gamaCommandBuilder.buildLegacy(Map.of("-hpc", "1"), pathToExperimentPlanXmlFile, pathToLocalExperimentOutputDir);

            // Execute legacy command.
            executeLegacy(
                createXmlCommand,
                runLegacyCommand,
                pathToExperimentPlanXmlFile,
                experimentReq.getExperiment(),
                finalStep,
                pathToLocalExperimentOutputDir,
                experimentReq.getExperimentResult(),
                gamaParams
            );
        });
    }

    private String getPathToExperimentPlanXmlFile(
        String nodeId,
        BigInteger userId,
        BigInteger projectId,
        BigInteger modelId,
        BigInteger experimentId,
        BigInteger experimentResultNumber,
        String gamlFileName,
        String experimentName
    ) {
        return String.format("%s/node-%s_user-%s_project-%s_model-%s_experiment-%s_result-%s_%s-%s.xml",
            GAMA_XML_FILE_PATH, nodeId, userId, projectId, modelId, experimentId, experimentResultNumber, gamlFileName, experimentName);
    }

    @Override
    public String getPathToLocalExperimentOutputDir(
        String nodeId,
        BigInteger userId,
        BigInteger projectId,
        BigInteger modelId,
        BigInteger experimentId,
        BigInteger experimentResultNumber,
        String gamlFileName,
        String experimentName
    ) {
        return String.format("%s/node-%s_user-%s_project-%s_model-%s_experiment-%s_result-%s_%s-%s",
            GAMA_OUTPUT_DIR, nodeId, userId, projectId, modelId, experimentId, experimentResultNumber, gamlFileName, experimentName);
    }

    /**
     * This method is used to run job execute legacy gama command.
     *
     * @param createXmlCommand String
     * @param runLegacyCommand String
     * @param pathToExperimentPlanXmlFile String
     * @param experiment Experiment
     * @param finalStep long
     * @param pathToLocalExperimentOutputDir String
     * @param experimentResult ExperimentResult
     */
    private void executeLegacy(
        String createXmlCommand,
        String runLegacyCommand,
        String pathToExperimentPlanXmlFile,
        Experiment experiment,
        long finalStep,
        String pathToLocalExperimentOutputDir,
        ExperimentResult experimentResult,
        Map<String, String> gamaParams
    ) {
        try {
            final var experimentId = experiment.getId();
            final var experimentName = experiment.getName();
            long start = timeUtil.getCurrentTimeNano();
            experimentResultService.updateStatus(experimentResult, ExperimentResultStatusConst.IN_PROGRESS);

            if (gamaParams != null) {
                gamaParams.put("Run_id", experimentResult.getSimulationRunId().toString());
            }

            virtualThreadExecutor.submit(() -> {
                metricsCollectionService.startCollecting(experimentResult.getSimulationRunId());
//                experimentResult.setRunCommandPid(ProcessHandle.current().pid());
                // Execute commands
                final var executeCommandFuture = gamaCommandExecutor.executeLegacy(createXmlCommand, runLegacyCommand,
                    pathToExperimentPlanXmlFile, experimentId, experimentName, finalStep, experimentResult, gamaParams);

                executeCommandFuture.whenComplete((executeCommandResult, executeCommandError) -> {
                    if (executeCommandError != null) {
                        log.error("Error while running simulation", executeCommandError);

                        metricsCollectionService.stopCollecting(experimentResult.getSimulationRunId());
                        clearLocalResource(pathToLocalExperimentOutputDir);
                        clearLocalResource(pathToExperimentPlanXmlFile);
                        experimentResult.setRunCommandPid(null); // Clear run command pid.
                        experimentResultService.updateStatus(experimentResult, ExperimentResultStatusConst.FAILED);

                        return;
                    }

                    long runTime = timeUtil.getCurrentTimeNano() - start;
                    experimentResult.setRunTime(runTime);

                    metricsCollectionService.stopCollecting(experimentResult.getSimulationRunId());

                    log.info("Simulation completed for experiment: {}", experimentId);
//                    clearLocalResource(pathToExperimentPlanXmlFile);
                    // Sleep for 1 second to make sure all images are loaded.
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        log.error("Error while sleeping", e);
                    }
                    final var imageList = imageService.listImagesInDirectory(pathToLocalExperimentOutputDir + "/snapshot");
                    saveResult(experimentResult, imageList, pathToLocalExperimentOutputDir);
                });

                executeCommandFuture.join();
            });
        } catch (Exception e) {
            log.error("Error while executing legacy command: ", e);
        }
    }

    /**
     * This method is used to clear local resource.
     *
     * @param dir String
     */
    private void clearLocalResource(String dir) {
        // Clear old output directory
        final var processBuilder = new ProcessBuilder();

        log.info("Start clear old output directory: {}", dir);
        try {
            final var process = processBuilder.command("bash", "-c", "rm -rf " + dir + "*").start();

            log.info("Successfully clear old output directory: {}", dir);
        } catch (Exception e) {
//            throw new CannotClearOldSimulationOutputException(e.getMessage());
            log.error("Error while clearing old output directory: {}", dir);
        }
    }

    /**
     * This method is used to clear local resource.
     *
     * @param dir String
     * @return CompletableFuture<Void>
     */
    public CompletableFuture<Void> waitForClearLocalResource(String dir) {
        return CompletableFuture.runAsync(() -> {
            // Clear old output directory
            final var processBuilder = new ProcessBuilder();
            log.info("Start clear old output directory: " + dir);

            try {
                processBuilder.command("bash", "-c", "rm -rf " + dir + "*").start().waitFor();
                log.info("Successfully clear old output directory: " + dir);
            } catch (Exception e) {
                throw new CannotClearOldSimulationOutputException(e.getMessage());
            }
        }, virtualThreadExecutor);
    }

    /**
     * This method is used to save result after simulation.
     *
     * @param experimentResult ExperimentResult
     * @param imageList List<List<String>>
     * @param experimentResultLocation String
     */
    private void saveResult(
        ExperimentResult experimentResult,
        List<List<String>> imageList,
        String experimentResultLocation
    ) {
        long start = timeUtil.getCurrentTimeNano();
        // Save new experiment result images.
        final List<ExperimentResultImage> experimentResultImageList = new ArrayList<>();
        final var imageCategoryList = imageList.stream().map(image -> image.get(2)).distinct().toList();
        final var experimentResultCategoryList = imageCategoryList.stream().map(imageCategory -> ExperimentResultCategory.builder()
            .name(imageCategory)
            .experimentResult(experimentResult)
            .build()).toList();

        final var experimentResultCategories = experimentResultCategoryRepository.saveAll(experimentResultCategoryList);
        final var resultCategoryNames = experimentResultCategories.stream().map(ExperimentResultCategory::getName).toList();

        imageList.forEach(image -> {
            final var imageName = image.getFirst();
            final var imageExtension = image.get(1);
            final var imageCategory = image.get(2);
            final var imageStep = !image.get(3).isEmpty() ? convertUtil.convertStringToInteger(image.get(3)) : null;
            final var experimentResultCategory = experimentResultCategories.stream()
                .filter(category -> category.getName().equals(imageCategory))
                .findFirst()
                .orElse(null);

            experimentResultImageList.add(ExperimentResultImage.builder()
                .name(imageName)
                .location(experimentResultLocation + "/snapshot/" + imageName)
                .extension(imageExtension)
                .experimentResult(experimentResult)
                .experimentResultCategory(experimentResultCategory)
                .step(imageStep)
                .build());
        });
        experimentResultImageRepository.saveAll(experimentResultImageList);

        long end = timeUtil.getCurrentTimeNano();
        log.info("Save experiment result images took: {} ns", end - start);

        experimentResult.setRunCommandPid(null); // Set run command pid to null after saving images.
        // Update experiment result status.
        experimentResultService.updateStatus(experimentResult, ExperimentResultStatusConst.FINISHED);

        // Prepare for download.
        prepareForDownload(experimentResult, experimentResultLocation, resultCategoryNames);
    }

    /**
     * This method is used to prepare for download.
     *
     * @param experimentResult ExperimentResult
     * @param resultLocation String
     */
    private void prepareForDownload(
        ExperimentResult experimentResult,
        String resultLocation,
        List<String> resultCategoryNames
    ) {
        virtualThreadExecutor.submit(() -> {
            if (experimentResult.getStatus() != ExperimentResultStatusConst.FINISHED) {
                log.error("Experiment result is not finished, cannot prepare for download");
                return;
            }

            final var isSuccess = fileUtil.zipFolder(resultLocation);
            if (!isSuccess) return;

            experimentResultService.updateStatus(experimentResult, ExperimentResultStatusConst.READY_FOR_DOWNLOAD);
        });
    }

    private boolean separateResultImagesIntoCategoryFolder(String copyResultLocation, List<String> resultCategoryNames) {
        final var sourceDir = Paths.get(copyResultLocation, "snapshot");

        if (!Files.exists(sourceDir) || !Files.isDirectory(sourceDir)) {
            System.err.println("Source directory does not exist or is not a directory: " + sourceDir);
            return false;
        }

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(sourceDir)) {
            for (Path entry : stream) {
                if (Files.isRegularFile(entry)) {
                    String fileName = entry.getFileName().toString();

                    for (String category : resultCategoryNames) {
                        if (fileName.startsWith(category + "-")) {
                            Path categoryDir = sourceDir.resolve(category);
                            if (!Files.exists(categoryDir)) {
                                Files.createDirectory(categoryDir);
                            }

                            Path targetPath = categoryDir.resolve(fileName);
                            Files.move(entry, targetPath, StandardCopyOption.REPLACE_EXISTING);
                            System.out.println("Moved " + fileName + " to " + categoryDir);
                            break;
                        }
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error occurred while processing files: " + e.getMessage());
            return false;
        }

        return true;
    }

//    /**
//     * This method is used to clear old S3 result.
//     *
//     * @param projectId BigInteger
//     * @param modelId BigInteger
//     * @param experimentId BigInteger
//     * @param experimentResultId int
//     */
//    private void clearOldS3Result(
//        BigInteger projectId,
//        BigInteger modelId,
//        BigInteger experimentId,
//        int experimentResultId
//    ) {
//        final var outputObjectKey = getOutputObjectKey(projectId, modelId, experimentId, experimentResultId);
//
//        s3Service.clear(outputObjectKey);
//    }

//    /**
//     * This method is used to upload result to S3.
//     *
//     * @param projectId BigInteger
//     * @param modelId BigInteger
//     * @param experimentId BigInteger
//     * @param experimentResultId int
//     * @param localOutputDir String
//     */
//    private void uploadResult(
//        BigInteger projectId,
//        BigInteger modelId,
//        BigInteger experimentId,
//        int experimentResultId,
//        String localOutputDir
//    ) {
//        log.info("Start get S3 object key");
//        final var outputObjectKey = getOutputObjectKey(projectId, modelId, experimentId, experimentResultId);
//        log.info("End get S3 object key: {}", outputObjectKey);
//
//        log.info("Start upload directory to S3");
//        s3Service.uploadDirectory(localOutputDir, outputObjectKey);
//        log.info("End upload directory to S3");
//    }

//    /**
//     * This method is used to get output object key.
//     *
//     * @param projectId BigInteger
//     * @param modelId BigInteger
//     * @param experimentId BigInteger
//     * @param experimentResultId int
//     * @return String
//     */
//    private String getOutputObjectKey(
//        BigInteger projectId,
//        BigInteger modelId,
//        BigInteger experimentId,
//        int experimentResultId
//    ) {
//        final var userId = authService.getCurrentUserId();
//
//        return String.format("simulation_results/user_%s/project_%s/model_%s/experiment_%s/result_%s/",
//            projectId, userId, modelId, experimentId, experimentResultId);
//    }
}
