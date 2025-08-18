package com.uet.agent_simulation_api.exceptions.model;

public class ModelNotFoundException extends RuntimeException {
    public ModelNotFoundException(String message) {
        super(message);
    }
}
