package com.uet.agent_simulation_api.exceptions.handlers;

import com.uet.agent_simulation_api.exceptions.ErrorResponse;
import com.uet.agent_simulation_api.exceptions.errors.ProjectErrors;
import com.uet.agent_simulation_api.exceptions.project.ProjectNotFoundException;
import com.uet.agent_simulation_api.responses.ResponseHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
@RequiredArgsConstructor
public class ProjectExceptionHandler {
    private final ResponseHandler responseHandler;

    /**
     * Handle ProjectNotFoundException
     *
     * @param e ProjectNotFoundException
     * @return ResponseEntity<ErrorResponse>
     */
    @ExceptionHandler(ProjectNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleProjectNotFoundException(ProjectNotFoundException e) {
        return responseHandler.respondError(e, ProjectErrors.E_PJ_0001);
    }
}
