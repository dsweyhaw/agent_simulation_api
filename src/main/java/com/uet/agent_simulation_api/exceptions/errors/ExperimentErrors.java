package com.uet.agent_simulation_api.exceptions.errors;

import org.springframework.http.HttpStatus;

/**
 * This class is used to store experiment errors.
 */
public final class ExperimentErrors {
    public static final ErrorDetails E_EXP_0001 = new ErrorDetails(HttpStatus.NOT_FOUND,
            "E_EXP_0001", "Experiment not found");
}
