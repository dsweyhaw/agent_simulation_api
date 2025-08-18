package com.uet.agent_simulation_api.services.model;

import com.uet.agent_simulation_api.exceptions.errors.ModelErrors;
import com.uet.agent_simulation_api.exceptions.model.ModelNotFoundException;
import com.uet.agent_simulation_api.models.Model;
import com.uet.agent_simulation_api.repositories.ModelRepository;
import com.uet.agent_simulation_api.services.auth.IAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ModelService implements IModelService {
    private final IAuthService authService;
    private final ModelRepository modelRepository;

    @Override
    public List<Model> get(BigInteger projectId, Boolean hasExperiment) {
        if (hasExperiment != null) {
            return modelRepository.findByExperimentNotNull(authService.getCurrentUserId(), projectId);
        }

        return modelRepository.find(authService.getCurrentUserId(), projectId);
    }

    @Override
    public Model getModel(BigInteger modelId) {
        return modelRepository.findById(modelId).orElseThrow(
            () -> new ModelNotFoundException(ModelErrors.E_MODEL_0001.defaultMessage())
        );
    }
}
