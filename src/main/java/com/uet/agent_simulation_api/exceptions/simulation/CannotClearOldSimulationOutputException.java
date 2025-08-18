package com.uet.agent_simulation_api.exceptions.simulation;

public class CannotClearOldSimulationOutputException extends RuntimeException {
    public CannotClearOldSimulationOutputException(String message) {
        super(message);
    }
}
