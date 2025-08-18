package com.uet.agent_simulation_api.exceptions.errors;

import org.springframework.http.HttpStatus;

/**
 * This class is used to store node errors.
 */
public final class NodeErrors {
    public static final ErrorDetails E_NODE_0001 = new ErrorDetails(HttpStatus.NOT_FOUND,
            "E_NODE_0001", "Node not found");

    public static final ErrorDetails E_NODE_0002 = new ErrorDetails(HttpStatus.SERVICE_UNAVAILABLE,
            "E_NODE_0002", "Cannot create connection to node");

    public static final ErrorDetails E_NODE_0003 = new ErrorDetails(HttpStatus.BAD_GATEWAY,
            "E_NODE_0003", "Cannot fetch data from node");
}
