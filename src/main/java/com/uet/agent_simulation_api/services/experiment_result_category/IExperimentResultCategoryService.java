package com.uet.agent_simulation_api.services.experiment_result_category;

import com.uet.agent_simulation_api.models.ExperimentResultCategory;

import java.math.BigInteger;
import java.util.List;

public interface IExperimentResultCategoryService {
    /**
     * This method is used to get all experiment result categories.
     *
     * @param experimentResultId Experiment result id
     * @param experimentId       Experiment id
     * @param modelId            Model id
     * @param projectId          Project id
     * @return List of ExperimentResultCategory
     */
    List<ExperimentResultCategory> get(
        BigInteger experimentResultId,
        BigInteger experimentId,
        BigInteger modelId,
        BigInteger projectId
    );
}
