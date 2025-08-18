package com.uet.agent_simulation_api.exceptions;

/**
 * Error response class.
 */
public record ErrorResponse(
    String status,
    int statusCode,
    String errorCode,
    String message,
    Object details
) { }
