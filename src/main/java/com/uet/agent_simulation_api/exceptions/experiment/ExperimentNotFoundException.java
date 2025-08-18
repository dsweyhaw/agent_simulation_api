package com.uet.agent_simulation_api.exceptions.experiment;

public class ExperimentNotFoundException extends RuntimeException {
    public ExperimentNotFoundException(String message) {
        super(message);
    }
}
