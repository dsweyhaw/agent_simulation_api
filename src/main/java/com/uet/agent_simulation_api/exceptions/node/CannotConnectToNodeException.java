package com.uet.agent_simulation_api.exceptions.node;

public class CannotConnectToNodeException extends RuntimeException {
    public CannotConnectToNodeException(String message) {
        super(message);
    }
}
