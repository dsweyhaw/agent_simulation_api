package com.uet.agent_simulation_api.services.experiment_result_category;

import com.uet.agent_simulation_api.models.ExperimentResultCategory;
import com.uet.agent_simulation_api.repositories.ExperimentResultCategoryRepository;
import com.uet.agent_simulation_api.services.auth.IAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExperimentResultResultCategoryService implements IExperimentResultCategoryService {
    private final IAuthService authService;
    private final ExperimentResultCategoryRepository experimentResultCategoryRepository;

    @Override
    public List<ExperimentResultCategory> get(BigInteger experimentResultId, BigInteger experimentId,
          BigInteger modelId, BigInteger projectId) {
        return experimentResultCategoryRepository.find(experimentResultId, experimentId, modelId, projectId,
			authService.getCurrentUserId());
    }
}
