package com.uet.agent_simulation_api.exceptions.errors;

import org.springframework.http.HttpStatus;

/**
 * This class is used to store experiment result errors.
 */
public class ExperimentResultErrors {
    public static final ErrorDetails E_ER_0001 = new ErrorDetails(HttpStatus.NOT_FOUND,
            "E_ER_0001", "Experiment result not found");
}
