package com.uet.agent_simulation_api.services.experiment_result;

import com.uet.agent_simulation_api.exceptions.errors.ExperimentResultErrors;
import com.uet.agent_simulation_api.exceptions.experiment_result.ExperimentResultNotFoundException;
import com.uet.agent_simulation_api.exceptions.node.CannotFetchNodeDataException;
import com.uet.agent_simulation_api.models.ExperimentResult;
import com.uet.agent_simulation_api.repositories.ExperimentResultRepository;
import com.uet.agent_simulation_api.requests.simulation.CreateClusterSimulationRequest;
import com.uet.agent_simulation_api.responses.exeperiment_result.DownloadExperimentResultResponse;
import com.uet.agent_simulation_api.responses.exeperiment_result.ExperimentProgressResponse;
import com.uet.agent_simulation_api.responses.exeperiment_result.ExperimentResultDetailResponse;
import com.uet.agent_simulation_api.services.auth.IAuthService;
import com.uet.agent_simulation_api.services.node.INodeService;
import com.uet.agent_simulation_api.utils.FileUtil;
import com.uet.agent_simulation_api.utils.ThreadUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExperimentResultService implements IExperimentResultService {
    private final FileUtil fileUtil;
    private final ThreadUtil threadUtil;
    private final IAuthService authService;
    private final INodeService nodeService;
    private final ExperimentResultRepository experimentResultRepository;

    @Override
    public List<ExperimentResult> get(BigInteger experimentId, BigInteger modelId, BigInteger projectId, Integer nodeId) {
        return experimentResultRepository.find(authService.getCurrentUserId(), experimentId, modelId, projectId, nodeId, null);
    }

    @Override
    public ExperimentResultDetailResponse getDetails(BigInteger id) {
        final var experimentResult = experimentResultRepository.findExperimentResultDetail(id, authService.getCurrentUserId())
            .orElseThrow(() -> new ExperimentResultNotFoundException(ExperimentResultErrors.E_ER_0001.defaultMessage()));
        final var node = nodeService.getNodeById(experimentResult.getNodeId());
        final var downloadUrl = String.format("http://%s:%d/api/v1/experiment_results/%d/download",
                node.getHost(), node.getPort(), experimentResult.getId());

        return new ExperimentResultDetailResponse(
            experimentResult.getId(),
            experimentResult.getFinalStep(),
            experimentResult.getStatus(),
            experimentResult.getExperimentId(),
            experimentResult.getModelId(),
            experimentResult.getProjectId(),
            experimentResult.getNodeId(),
            experimentResult.getExperimentName(),
            experimentResult.getModelName(),
            experimentResult.getProjectName(),downloadUrl
        );
    }

    @Override
    public void updateStatus(ExperimentResult experimentResult, int status) {
        experimentResult.setStatus(status);

        experimentResultRepository.save(experimentResult);
    }

    @Override
    public ExperimentProgressResponse getExperimentProgress(BigInteger id) {
        final var experimentResult = experimentResultRepository.findById(id);
        if (experimentResult.isEmpty()) {
            throw new ExperimentResultNotFoundException(ExperimentResultErrors.E_ER_0001.defaultMessage());
        }

        if (!nodeService.getCurrentNodeId().equals(experimentResult.get().getNodeId())) {
            return getExperimentProgressFromNode(id, experimentResult.get().getNodeId());
        }

        if (!Files.exists(Paths.get(experimentResult.get().getLocation()))) {
            return new ExperimentProgressResponse(true, experimentResult.get().getStatus(), null,
                    experimentResult.get().getFinalStep());
        }

        final var consoleOutputFileName = fileUtil.getFileNameByPrefix(experimentResult.get().getLocation(), "console-output");
        if (consoleOutputFileName == null) {
            return new ExperimentProgressResponse(true, experimentResult.get().getStatus(), null,
                    experimentResult.get().getFinalStep());
        }

        final var simulationOutputLastLine = fileUtil.readLastLine(experimentResult.get().getLocation() + "/" + consoleOutputFileName, "step");

        var step = extractStep(simulationOutputLastLine);
        if (step < 0 || step == 0) {
            step = 0;
        } else if (!step.equals(experimentResult.get().getFinalStep())) {
            step -= 1;
        }

        return new ExperimentProgressResponse(false, experimentResult.get().getStatus(), step, experimentResult.get().getFinalStep());
    }

    private Integer extractStep(String line) {
        if (line == null) {
            return -1;
        }

        int stepNumber = -1;
        try {
            final var pattern = Pattern.compile("step\\s+(\\d+)");
            final var matcher = pattern.matcher(line);

            if (matcher.find()) {
                stepNumber = Integer.parseInt(matcher.group(1));
            }
        } catch (Exception e) {
            log.error("Error while extracting step number: {}", e.getMessage());
        }

        return stepNumber;
    }

    private ExperimentProgressResponse getExperimentProgressFromNode(BigInteger resultId, Integer nodeId) {
        final var webClient = nodeService.getWebClientByNodeId(nodeId);

        try {
            final var response = webClient.get().uri("/api/v1/experiment_results/" + resultId + "/progress")
                    .retrieve().bodyToMono(ExperimentProgressResponse.class).block();

            if (response == null) {
                throw new ExperimentResultNotFoundException(ExperimentResultErrors.E_ER_0001.defaultMessage());
            }

            return response;
        } catch (Exception e) {
            log.error("Error while fetching experiment progress: {}", e.getMessage());

            throw new CannotFetchNodeDataException(e.getMessage());
        }
    }

    @Override
    public DownloadExperimentResultResponse download(BigInteger id) {
        final var experimentResult = experimentResultRepository.findById(id);
        if (experimentResult.isEmpty()) {
            throw new ExperimentResultNotFoundException(ExperimentResultErrors.E_ER_0001.defaultMessage());
        }

        if (!nodeService.getCurrentNodeId().equals(experimentResult.get().getNodeId())) {
            throw new ExperimentResultNotFoundException("Experiment result is not in current node");
        }

        final var path = Paths.get(experimentResult.get().getLocation() + ".zip");
        final var file = path.toFile();
        InputStreamResource resource;
        try {
            resource = new InputStreamResource(new FileInputStream(file));
        } catch (Exception e) {
            throw new ExperimentResultNotFoundException(ExperimentResultErrors.E_ER_0001.defaultMessage());
        }

        return new DownloadExperimentResultResponse(resource, file.getName(), file.length());
    }

    @Override
    public void stop(BigInteger id) {
        final var experimentResult = experimentResultRepository.findById(id);
        if (experimentResult.isEmpty()) {
            throw new ExperimentResultNotFoundException(ExperimentResultErrors.E_ER_0001.defaultMessage());
        }

        if (!nodeService.getCurrentNodeId().equals(experimentResult.get().getNodeId())) {
            final var webClient = nodeService.getWebClientByNodeId(experimentResult.get().getNodeId());
            webClient.delete().uri("/api/v1/experiment_results/" + id + "/stop").retrieve().bodyToMono(String.class).block();

            return;
        }

        if (experimentResult.get().getRunCommandPid() == null) {
            return;
        }

        threadUtil.killProcessById(experimentResult.get().getRunCommandPid());
    }

    @Override
    public CreateClusterSimulationRequest generateRequestNumber(CreateClusterSimulationRequest request) {
        AtomicInteger startValue = new AtomicInteger(1);

        request.getSimulationRequests().forEach(simulationRequest -> {
            simulationRequest.setNumber(startValue.getAndIncrement());
        });

        return request;
    }

    @Override
    public BigInteger getLastExperimentResultNumber(BigInteger experimentId) {
        return experimentResultRepository.getLastExperimentResultNumber(experimentId, authService.getCurrentUserId());
    }

    @Override
    public void delete(BigInteger id) {
        final var experimentResult = experimentResultRepository.findById(id);
        if (experimentResult.isEmpty()) {
            return;
        }

        fileUtil.delete(experimentResult.get().getLocation());
        fileUtil.delete(experimentResult.get().getLocation() + ".zip");
        experimentResultRepository.delete(experimentResult.get());
    }
}
