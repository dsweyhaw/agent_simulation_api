package com.uet.agent_simulation_api.services.experiment;

import com.uet.agent_simulation_api.exceptions.errors.ExperimentErrors;
import com.uet.agent_simulation_api.exceptions.experiment.ExperimentNotFoundException;
import com.uet.agent_simulation_api.models.Experiment;
import com.uet.agent_simulation_api.repositories.ExperimentRepository;
import com.uet.agent_simulation_api.services.auth.IAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExperimentService implements IExperimentService {
    private final IAuthService authService;
    private final ExperimentRepository experimentRepository;

    @Override
    public List<Experiment> get(BigInteger projectId, BigInteger modelId) {
        return experimentRepository.find(authService.getCurrentUserId(), projectId, modelId);
    }

    @Override
    public Experiment getExperiment(BigInteger experimentId) {
        return experimentRepository.findById(experimentId).orElseThrow(
            () -> new ExperimentNotFoundException(ExperimentErrors.E_EXP_0001.defaultMessage())
        );
    }
}
