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
    
    /**
     * This method is used to delete a model by id.
     * This will also delete the model file from the file system.
     *
     * @param modelId Model id to delete
     * @param projectId Project id that the model belongs to
     * @return boolean - true if deletion was successful, false otherwise
     */
    boolean deleteModel(BigInteger modelId, BigInteger projectId);
}
