package com.uet.agent_simulation_api.exceptions.errors;

import org.springframework.http.HttpStatus;

/**
 * This class is used to store model errors.
 */
public final class ModelErrors {
    public static final ErrorDetails E_MODEL_0001 = new ErrorDetails(HttpStatus.NOT_FOUND,
            "E_MODEL_0001", "Model not found");
}
