package com.uet.agent_simulation_api.exceptions.errors;

import org.springframework.http.HttpStatus;

/**
 * Error details class.
 */
public record ErrorDetails(
    HttpStatus httpStatus,
    String errorCode,
    String defaultMessage
) { }
