package com.uet.agent_simulation_api.services.model;

import com.uet.agent_simulation_api.models.Model;

import java.math.BigInteger;
import java.util.List;

public interface IModelService {
    /**
     * This method is used to get all models.
     *
     * @return List<Model>
     */
    List<Model> get(BigInteger projectId, Boolean hasExperiment);

    /**
     * This method is used to get a model by id.
     *
     * @param modelId
     * @return Model
     */
    Model getModel(BigInteger modelId);
}
