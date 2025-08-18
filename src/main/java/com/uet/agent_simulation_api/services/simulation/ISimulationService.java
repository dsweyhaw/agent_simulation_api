package com.uet.agent_simulation_api.services.simulation;

import com.uet.agent_simulation_api.requests.simulation.CreateSimulationRequest;

import java.math.BigInteger;

/**
 * Simulation service interface.
 */
public interface ISimulationService {
    /**
     * This method is used to run simulation.
     */
    void run();

    /**
     * This method is used to run simulation with a specific request.
     *
     * @param request CreateSimulationRequest
     */
    void run(CreateSimulationRequest request);

    /**
     * This method is used to get path to local experiment output directory.
     *
     * @param nodeId Node id
     * @param userId User id
     * @param projectId Project id
     * @param modelId Model id
     * @param experimentId Experiment id
     * @param experimentResultNumber Experiment result number
     * @param gamlFileName GAML file name
     * @param experimentName Experiment name
     * @return Path to local experiment output directory
     */
    String getPathToLocalExperimentOutputDir(
        String nodeId,
        BigInteger userId,
        BigInteger projectId,
        BigInteger modelId,
        BigInteger experimentId,
        BigInteger experimentResultNumber,
        String gamlFileName,
        String experimentName
    );
}
