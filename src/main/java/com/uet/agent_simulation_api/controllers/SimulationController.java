package com.uet.agent_simulation_api.controllers;

import com.uet.agent_simulation_api.constant.ExperimentResultStatusConst;
import com.uet.agent_simulation_api.constant.SimulationConst;
import com.uet.agent_simulation_api.exceptions.errors.ExperimentErrors;
import com.uet.agent_simulation_api.exceptions.errors.ModelErrors;
import com.uet.agent_simulation_api.exceptions.errors.NodeErrors;
import com.uet.agent_simulation_api.exceptions.errors.ProjectErrors;
import com.uet.agent_simulation_api.exceptions.experiment.ExperimentNotFoundException;
import com.uet.agent_simulation_api.exceptions.model.ModelNotFoundException;
import com.uet.agent_simulation_api.exceptions.node.NodeNotFoundException;
import com.uet.agent_simulation_api.exceptions.project.ProjectNotFoundException;
import com.uet.agent_simulation_api.models.ExperimentResult;
import com.uet.agent_simulation_api.pubsub.PubSubCommands;
import com.uet.agent_simulation_api.pubsub.message.master.simulation.RunSimulation;
import com.uet.agent_simulation_api.pubsub.publisher.MessagePublisher;
import com.uet.agent_simulation_api.repositories.ExperimentRepository;
import com.uet.agent_simulation_api.repositories.ExperimentResultRepository;
import com.uet.agent_simulation_api.repositories.ModelRepository;
import com.uet.agent_simulation_api.repositories.NodeRepository;
import com.uet.agent_simulation_api.repositories.ProjectRepository;
import com.uet.agent_simulation_api.requests.experiment_result.NewExperimentResult;
import com.uet.agent_simulation_api.requests.simulation.CreateClusterSimulationRequest;
import com.uet.agent_simulation_api.requests.simulation.CreateExperimentRequest;
import com.uet.agent_simulation_api.requests.simulation.CreateSimulationRequest;
import com.uet.agent_simulation_api.requests.simulation.RunMultiSimulationRequest;
import com.uet.agent_simulation_api.responses.ResponseHandler;
import com.uet.agent_simulation_api.responses.SuccessResponse;
import com.uet.agent_simulation_api.responses.simulation.RunMultiSimulationResponse;
import com.uet.agent_simulation_api.responses.simulation.RunSimulationResponse;
import com.uet.agent_simulation_api.services.auth.IAuthService;
import com.uet.agent_simulation_api.services.experiment.IExperimentService;
import com.uet.agent_simulation_api.services.experiment_result.IExperimentResultService;
import com.uet.agent_simulation_api.services.model.IModelService;
import com.uet.agent_simulation_api.services.multi_simulation.IMultiSimulationService;
import com.uet.agent_simulation_api.services.node.INodeService;
import com.uet.agent_simulation_api.services.project.ProjectService;
import com.uet.agent_simulation_api.services.simulation.ISimulationRunService;
import com.uet.agent_simulation_api.services.simulation.ISimulationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@RestController
@Slf4j
@RequestMapping("/api/v1/simulations")
@RequiredArgsConstructor
public class SimulationController {
    private final IAuthService authService;
    private final INodeService nodeService;
    private final IModelService modelService;
    private final ProjectService projectService;
    private final NodeRepository nodeRepository;
    private final ResponseHandler responseHandler;
    private final ModelRepository modelRepository;
    private final MessagePublisher messagePublisher;
    private final ProjectRepository projectRepository;
    private final IExperimentService experimentService;
    private final ISimulationService simulationService;
    private final ExperimentRepository experimentRepository;
    private final ISimulationRunService simulationRunService;
    private final IExperimentResultService experimentResultService;
    private final ExperimentResultRepository experimentResultRepository;
    private final IMultiSimulationService multiSimulationService;

    @GetMapping
    public ResponseEntity<SuccessResponse> getSimulationHistory(
        @RequestParam(name = "project_id") BigInteger projectId
    ) {
        return responseHandler.respondSuccess(simulationRunService.getSimulationHistory(projectId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<SuccessResponse> deleteSimulation(@PathVariable BigInteger id) {
        simulationRunService.delete(id);

        return responseHandler.respondSuccess("Simulation deleted successfully");
    }

    @PostMapping
    public ResponseEntity<SuccessResponse> runSimulation(@Valid @RequestBody CreateSimulationRequest request) {
        simulationService.run(request);

        final var resultIdList = new ArrayList<>();
        final var nodeId = nodeService.getCurrentNodeId();
        final var projectId = request.getProjectId();
        final var order = request.getOrder();

        request.getExperiments().forEach((experiment) -> {
            final var experimentId = experiment.getId();
            final var modelId = experiment.getModelId();
            final var userId = authService.getCurrentUserId();
            final var experimentResultList = experimentResultRepository.find(userId, experimentId, modelId, projectId, nodeId, null);
                experimentResultList.forEach((result) -> {
                    resultIdList.add(new RunSimulationResponse(nodeId, projectId, modelId, experimentId, result.getId(), order));
                });
        });

        return responseHandler.respondSuccess(resultIdList);
    }

    @PostMapping("/cluster")
    public ResponseEntity<SuccessResponse> runSimulationCluster(@Valid @RequestBody CreateClusterSimulationRequest request) {
        // LOG: Debug received parameters
        log.info("=== BACKEND RECEIVED CLUSTER REQUEST ===");
        log.info("Number of simulation requests: {}", request.getSimulationRequests().size());
        
        for (int i = 0; i < request.getSimulationRequests().size(); i++) {
            CreateSimulationRequest simReq = request.getSimulationRequests().get(i);
            log.info("Simulation Request [{}]:", i);
            log.info("  - ProjectId: {}, NodeId: {}, Order: {}", simReq.getProjectId(), simReq.getNodeId(), simReq.getOrder());
            log.info("  - Experiments count: {}", simReq.getExperiments() != null ? simReq.getExperiments().size() : 0);
            log.info("  - GamaParams: {}", simReq.getGamaParams());
            if (simReq.getGamaParams() != null && !simReq.getGamaParams().isEmpty()) {
                simReq.getGamaParams().forEach((key, value) -> 
                    log.info("    {} = {}", key, value));
            } else {
                log.warn("    GamaParams is null or empty!");
            }
        }
        
        request = experimentResultService.generateRequestNumber(request);

        // Prepare for run simulation in cluster
        final var newExperimentResultList = prepareForRunSimulationInCluster(request);

        // Create new experiment results
        final var resultIdList = createNewExperimentResult(newExperimentResultList, request);

        // Run simulations on current node first
        request.getSimulationRequests().stream()
                .filter(simulationRequest -> simulationRequest.getNodeId().equals(nodeService.getCurrentNodeId()))
                .forEach(simulationService::run);

        // Then publish messages to other nodes
        request.getSimulationRequests().stream()
                .filter(simulationRequest -> !simulationRequest.getNodeId().equals(nodeService.getCurrentNodeId()))
                .forEach(simulationRequest -> {
                    final var message = RunSimulation.builder()
                            .nodeId(simulationRequest.getNodeId())
                            .command(PubSubCommands.RUN_SIMULATION)
                            .simulation(simulationRequest)
                            .build();
                    messagePublisher.publish(message);
                });

        return responseHandler.respondSuccess(resultIdList);
    }

    private List<NewExperimentResult> prepareForRunSimulationInCluster(CreateClusterSimulationRequest request) {
        final ArrayList<NewExperimentResult> newExperimentResultList = new ArrayList<>();
        // Keep track of the last number used for each experiment ID
        final Map<BigInteger, BigInteger> experimentLastNumbers = new HashMap<>();
        AtomicBoolean clearOldMultiSimulationData = new AtomicBoolean(false);

        request.getSimulationRequests().forEach(simulationRequest -> {
            final var node = nodeRepository.findById(simulationRequest.getNodeId())
                    .orElseThrow(() -> new NodeNotFoundException(NodeErrors.E_NODE_0001.defaultMessage()));

            final var userId = authService.getCurrentUserId();
            final var projectId = simulationRequest.getProjectId();
            final var project = projectRepository.findById(projectId)
                    .orElseThrow(() -> new ProjectNotFoundException(ProjectErrors.E_PJ_0001.defaultMessage()));

            final var requestNumber = simulationRequest.getNumber();

            simulationRequest.getExperiments().forEach(experimentReq -> {
                // Get model
                final var model = modelRepository.findByModeIdAndProjectId(experimentReq.getModelId(), projectId)
                        .orElseThrow(() -> new ModelNotFoundException(ModelErrors.E_MODEL_0001.defaultMessage()));

                // Get experiment
                final var experiment = experimentRepository
                        .findByExperimentIdAndModelId(experimentReq.getId(), experimentReq.getModelId())
                        .orElseThrow(() -> new ExperimentNotFoundException(ExperimentErrors.E_EXP_0001.defaultMessage()));

                // Get or initialize the last number for this experiment
                BigInteger lastNumber = experimentLastNumbers.get(experiment.getId());
                if (lastNumber == null) {
                    BigInteger lastExperimentResultNumber = experimentResultService.getLastExperimentResultNumber(experiment.getId());
                    lastNumber = lastExperimentResultNumber == null ? BigInteger.ZERO : lastExperimentResultNumber;
                }

                // Increment the number and store it back in the map
                lastNumber = lastNumber.add(BigInteger.ONE);
                experimentLastNumbers.put(experiment.getId(), lastNumber);
                final var experimentResultNumber = lastNumber;

                experimentReq.setGamlFile(model.getName() + ".gaml");
                experimentReq.setExperiment(experiment);
                experimentReq.setExperimentResultNumber(experimentResultNumber);

                final var modelId = experimentReq.getModelId();
                final var experimentId = experimentReq.getId();
                final var gamlFile = experimentReq.getGamlFile();
                final var finalStep = experimentReq.getFinalStep() != null ? experimentReq.getFinalStep() : SimulationConst.DEFAULT_FINAL_STEP;
                final var gamlFileName = gamlFile.replace(".gaml", "");
                final var experimentName = experimentReq.getExperiment().getName();
                final var pathToLocalExperimentOutputDir = simulationService.getPathToLocalExperimentOutputDir(node.getId().toString(), userId, projectId,
                        modelId, experimentId, experimentResultNumber, gamlFileName, experimentName);

                newExperimentResultList.add(new NewExperimentResult(experimentReq.getExperiment(), finalStep,
                        pathToLocalExperimentOutputDir, experimentResultNumber, node, requestNumber));
            });
        });

        return newExperimentResultList;
    }

    private List<RunMultiSimulationResponse> createNewExperimentResult(List<NewExperimentResult> newExperimentResultList, CreateClusterSimulationRequest request) {
        final var simulationRun = simulationRunService.create();

        final var resultIdList = new ArrayList<RunMultiSimulationResponse>();
        final var experimentResultList = newExperimentResultList.stream()
            .map(newExperimentResult -> ExperimentResult.builder()
                .experiment(newExperimentResult.experiment())
                .location(newExperimentResult.pathToLocalExperimentOutputDir())
                .finalStep(newExperimentResult.finalStep().intValue())
                .node(newExperimentResult.node())
                .status(ExperimentResultStatusConst.PENDING)
                .number(newExperimentResult.experimentResultNumber())
                .simulationRunId(simulationRun.getId())
                .build())
            .toList();

        final var savedExperimentResultList = experimentResultRepository.saveAll(experimentResultList);

        request.getSimulationRequests().forEach(simulationRequest -> {
            final var nodeName = nodeService.getNodeById(simulationRequest.getNodeId()).getName();
            final var project = projectService.getProject(simulationRequest.getProjectId()).orElse(null);
            final var projectName = project != null ? project.getName() : null;
            final var title = simulationRequest.getGamaParams() == null ?
                null : "Pigpen" + simulationRequest.getGamaParams().get("Pigpen_id");

            simulationRequest.getExperiments().forEach(experimentRequest -> {
                final var modelName = modelService.getModel(experimentRequest.getModelId()).getName();
                final var experimentName = experimentService.getExperiment(experimentRequest.getId()).getName();

                final var experimentResultNumber = experimentRequest.getExperimentResultNumber();
                final ExperimentResult experimentResult = savedExperimentResultList.stream()
                        .filter(result -> result.getNumber().equals(experimentResultNumber)
                                && result.getExperiment().getId().equals(experimentRequest.getId()))
                        .findFirst().orElse(null);

                final var experimentResultId = experimentResult != null ? experimentResult.getId() : null;
                experimentRequest.setExperimentResult(experimentResult);

                resultIdList.add(
                    new RunMultiSimulationResponse(
                        simulationRequest.getNodeId(),
                        simulationRequest.getProjectId(),
                        experimentRequest.getModelId(),
                        experimentRequest.getId(),
                        experimentResultId,
                        simulationRequest.getOrder(),
                        nodeName,
                        projectName,
                        modelName,
                        experimentName,
                        title
                    )
                );
            });
        });

        return resultIdList;
    }

    @PostMapping("/cluster/multi_simulation")
    public ResponseEntity<SuccessResponse> runMultiSimulation(@Valid @RequestBody RunMultiSimulationRequest request) {
        final var createSimulationRequest = prepareDataForRunMultiSimulation(request);

        return runSimulationCluster(createSimulationRequest);
    }

    private CreateClusterSimulationRequest prepareDataForRunMultiSimulation(RunMultiSimulationRequest request) {
        final var finalStep = request.getFinalStep();
        final var numberPigpen = request.getNumberPigpen();
        final var numberPigs = request.getNumberPigs();
        final var initDiseaseAppearPigpenIdList = request.getInitDiseaseAppearPigpenIds().split(",");
        final var initDiseaseAppearDays = request.getInitDiseaseAppearDays().split(",");
        final Map<String, String> diseaseAppearMap = IntStream.range(0, initDiseaseAppearPigpenIdList.length)
                .boxed()
                .collect(Collectors.toMap(
                        i -> initDiseaseAppearPigpenIdList[i],
                        i -> initDiseaseAppearDays[i]
                ));

        final var experiment = experimentRepository.findByModelName("multi-simulator");
        final var experimentId = experiment.getId();
        final var modelId = experiment.getModelId();
        final var projectId = experiment.getProjectId();

        final var pigCounts = Arrays.stream(numberPigs.split(","))
                .map(Integer::parseInt)
                .toList();
        final var pigIdList = new ArrayList<String>();
        int currentPigId = 1;
        for (Integer pigCount : pigCounts) {
            List<String> pigIds = new ArrayList<>();
            for (int i = 0; i < pigCount; i++) {
                pigIds.add(String.valueOf(currentPigId));
                currentPigId++;
            }
            pigIdList.add(String.join(",", pigIds));
        }

        final var pigpenIds = IntStream.rangeClosed(1, numberPigpen).boxed().toList();
        final var neighborIds = new ArrayList<String>();
        for (int i = 0; i < pigpenIds.size(); i++) {
            List<String> neighbors = new ArrayList<>();

            if (i > 0) {
                neighbors.add(String.valueOf(pigpenIds.get(i - 1)));
            }

            if (i < pigpenIds.size() - 1) {
                neighbors.add(String.valueOf(pigpenIds.get(i + 1)));
            }

            neighborIds.add(String.join(",", neighbors));
        }

        final var allNodeIdList = nodeService.getAllNodeIds();
        final var distributedNodeIds = IntStream.range(0, pigpenIds.size())
                .mapToObj(i -> Integer.parseInt(String.valueOf(allNodeIdList.get(i % allNodeIdList.size()))))
                .toList();

        final String allPigpenIdsString = pigpenIds.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));

        final List<CreateSimulationRequest> simulationRequests = new ArrayList<>();
        for (int i = 0; i < pigpenIds.size(); i++) {
            final Map<String, String> gamaParams = new HashMap<>();
            gamaParams.put("Pigpen_id", String.valueOf(pigpenIds.get(i)));
            gamaParams.put("Neighbor_ids", neighborIds.get(i));
            gamaParams.put("All_pigpen_ids", allPigpenIdsString);
            gamaParams.put("Pig_ids", pigIdList.get(i));
            gamaParams.put("Init_disease_appear_day", diseaseAppearMap.getOrDefault(String.valueOf(pigpenIds.get(i)), "-1"));

            final var experimentRequest = new CreateExperimentRequest();
            experimentRequest.setId(experimentId);
            experimentRequest.setModelId(modelId);
            experimentRequest.setFinalStep(Long.valueOf(finalStep));

            final var simulationRequest = new CreateSimulationRequest();
            simulationRequest.setNodeId(distributedNodeIds.get(i));
            simulationRequest.setProjectId(projectId);
            simulationRequest.setOrder(i + 1);
            simulationRequest.setGamaParams(gamaParams);
            simulationRequest.setExperiments(List.of(experimentRequest));

            simulationRequests.add(simulationRequest);
        }

        return new CreateClusterSimulationRequest(simulationRequests);
    }

    /**
     * Get available parameters for a GAML experiment
     *
     * @param projectId BigInteger
     * @param modelId BigInteger  
     * @param experimentId BigInteger
     * @return ResponseEntity<SuccessResponse>
     */
    @GetMapping("/experiments/{experimentId}/parameters")
    public ResponseEntity<SuccessResponse> getExperimentParameters(
            @RequestParam("project_id") BigInteger projectId,
            @RequestParam("model_id") BigInteger modelId,
            @PathVariable BigInteger experimentId) {
        
        // Return the standard tsunami parameters
        Map<String, Object> parameters = Map.of(
            "locals_number", Map.of("type", "int", "min", 0, "max", 10000, "default", 200, "description", "Number of locals"),
            "tourists_number", Map.of("type", "int", "min", 0, "max", 5000, "default", 100, "description", "Number of tourists"),
            "rescuers_number", Map.of("type", "int", "min", 0, "max", 1000, "default", 20, "description", "Number of rescuers"),
            "tourist_strategy", Map.of("type", "string", "options", List.of("wandering", "following rescuers or locals", "following crowd"), "default", "following rescuers or locals", "description", "Tourist Movement Strategy"),
            "car_strategy", Map.of("type", "string", "options", List.of("always go ahead", "go out when congestion"), "default", "always go ahead", "description", "Car Movement Strategy"),
            "tsunami_nb_segments", Map.of("type", "int", "min", 1, "max", 50, "default", 30, "description", "Tsunami segments"),
            "tsunami_approach_time", Map.of("type", "int", "min", 0, "max", 1000, "default", 460, "description", "Tsunami approach time"),
            "tsunami_speed_avg", Map.of("type", "float", "min", 10.0, "max", 100.0, "default", 44.3, "description", "Average tsunami speed")
        );
        
        return responseHandler.respondSuccess(parameters);
    }
}