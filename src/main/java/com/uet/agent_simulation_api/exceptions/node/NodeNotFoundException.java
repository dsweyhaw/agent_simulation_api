package com.uet.agent_simulation_api.exceptions.node;

public class NodeNotFoundException extends RuntimeException {
    public NodeNotFoundException(String message) {
        super(message);
    }
}
