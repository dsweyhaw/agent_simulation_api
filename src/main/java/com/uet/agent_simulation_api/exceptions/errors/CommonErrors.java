package com.uet.agent_simulation_api.exceptions.errors;

import org.springframework.http.HttpStatus;

/**
 * This class is used to store common errors.
 */
public final class CommonErrors {
    // Internal server error
    public static final ErrorDetails E0001 = new ErrorDetails(HttpStatus.INTERNAL_SERVER_ERROR,
            "E0001", "Internal Server Error");

    // Validation error
    public static final ErrorDetails E0002 = new ErrorDetails(HttpStatus.BAD_REQUEST,
            "E0002", "Validation Error");

    // Unauthorized error - E0003 - 401
    public static final ErrorDetails E0003 = new ErrorDetails(HttpStatus.UNAUTHORIZED,
            "E0003", "Unauthorized");

    // Access denied error - E0004 - 403
    public static final ErrorDetails E0004 = new ErrorDetails(HttpStatus.FORBIDDEN,
            "E0004", "Access Denied");

    // Login fail - E0005 - 401
    public static final ErrorDetails E0005 = new ErrorDetails(HttpStatus.UNAUTHORIZED,
            "E0005", "Email or password is incorrect");
}
