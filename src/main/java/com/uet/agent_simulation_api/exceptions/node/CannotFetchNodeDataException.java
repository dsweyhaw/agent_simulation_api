package com.uet.agent_simulation_api.exceptions.node;

public class CannotFetchNodeDataException extends RuntimeException {
    public CannotFetchNodeDataException(String message) {
        super(message);
    }
}
