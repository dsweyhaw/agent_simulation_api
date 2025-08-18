package com.uet.agent_simulation_api.services.experiment;

import com.uet.agent_simulation_api.models.Experiment;

import java.math.BigInteger;
import java.util.List;

public interface IExperimentService {
    /**
     * This method is used to get all experiments.
     *
     * @param projectId Project id
     * @param modelId   Model id
     * @return List<Experiment>
     */
    List<Experiment> get(BigInteger projectId, BigInteger modelId);

    /**
     * This method is used to get an experiment by id.
     *
     * @param experimentId
     * @return Experiment
     */
    Experiment getExperiment(BigInteger experimentId);
}
