package com.uet.agent_simulation_api.services.experiment_result;

import com.uet.agent_simulation_api.models.ExperimentResult;
import com.uet.agent_simulation_api.requests.simulation.CreateClusterSimulationRequest;
import com.uet.agent_simulation_api.responses.exeperiment_result.DownloadExperimentResultResponse;
import com.uet.agent_simulation_api.responses.exeperiment_result.ExperimentProgressResponse;
import com.uet.agent_simulation_api.responses.exeperiment_result.ExperimentResultDetailResponse;

import java.math.BigInteger;
import java.util.List;

public interface IExperimentResultService {
    /**
     * This method is used to get all experiment results.
     *
     * @param experimentId Experiment id
     * @param modelId      Model id
     * @param projectId    Project id
     * @param nodeId       Node id
     * @return List of ExperimentResult
     */
    List<ExperimentResult> get(
        BigInteger experimentId,
        BigInteger modelId,
        BigInteger projectId,
        Integer nodeId
    );

    /**
     * This method is used to get an experiment result detail.
     *
     * @param id Experiment result id
     * @return ExperimentResultDetailResponse
     */
    ExperimentResultDetailResponse getDetails(BigInteger id);

    /**
     * This method is used to update an experiment result status.
     *
     * @param experimentResult Experiment result
     * @param status Status
     */
    void updateStatus(ExperimentResult experimentResult, int status);

    /**
     * This method is used to check experiment progress.
     *
     * @param id BigInteger
     * @return ExperimentProgressResponse
     */
    ExperimentProgressResponse getExperimentProgress(BigInteger id);

    /**
     * This method is used to download experiment result.
     *
     * @param id BigInteger
     * @return DownloadExperimentResultResponse
     */
    DownloadExperimentResultResponse download(BigInteger id);

    /**
     * Stops the process or simulation associated with the specified ID.
     *
     * @param id The unique identifier of the process to stop
     */
    void stop(BigInteger id);

    /**
     * Generates and assigns a request number for the cluster simulation request.
     *
     * @param request The simulation request to process
     * @return The updated simulation request with generated request number
     */
    CreateClusterSimulationRequest generateRequestNumber(CreateClusterSimulationRequest request);

    /**
     * Retrieves the most recent result number for a given experiment.
     *
     * @param experimentId The unique identifier of the experiment
     * @return The last result number associated with the experiment
     */
    BigInteger getLastExperimentResultNumber(BigInteger experimentId);

    /**
     * Deletes the experiment result with the specified ID.
     *
     * @param id The unique identifier of the experiment result to delete
     */
    void delete(BigInteger id);
}
