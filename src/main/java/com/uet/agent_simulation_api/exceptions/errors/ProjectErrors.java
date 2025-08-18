package com.uet.agent_simulation_api.exceptions.errors;

import org.springframework.http.HttpStatus;

/**
 * This class is used to store project errors.
 */
public final class ProjectErrors {
    public static final ErrorDetails E_PJ_0001 = new ErrorDetails(HttpStatus.NOT_FOUND,
            "E_PJ_0001", "Project not found");
}
