package com.uet.agent_simulation_api.exceptions.handlers;

import com.uet.agent_simulation_api.exceptions.ErrorResponse;
import com.uet.agent_simulation_api.exceptions.errors.ExperimentErrors;
import com.uet.agent_simulation_api.exceptions.experiment.ExperimentNotFoundException;
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
public class ExperimentExceptionHandler {
    private final ResponseHandler responseHandler;

    /**
     * Handle ExperimentNotFoundException
     *
     * @param e ExperimentNotFoundException
     * @return ResponseEntity<ErrorResponse>
     */
    @ExceptionHandler(ExperimentNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleExperimentNotFoundException(ExperimentNotFoundException e) {
        return responseHandler.respondError(e, ExperimentErrors.E_EXP_0001);
    }
}
