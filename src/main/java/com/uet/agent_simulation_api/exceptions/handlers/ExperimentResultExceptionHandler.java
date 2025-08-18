package com.uet.agent_simulation_api.exceptions.handlers;

import com.uet.agent_simulation_api.exceptions.ErrorResponse;
import com.uet.agent_simulation_api.exceptions.errors.ExperimentResultErrors;
import com.uet.agent_simulation_api.exceptions.experiment_result.ExperimentResultNotFoundException;
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
public class ExperimentResultExceptionHandler {
    private final ResponseHandler responseHandler;

    /**
     * Handle ExperimentResultNotFoundException
     *
     * @param e ExperimentResultNotFoundException
     * @return ResponseEntity<ErrorResponse>
     */
    @ExceptionHandler(ExperimentResultNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleExperimentResultNotFoundException(ExperimentResultNotFoundException e) {
        return responseHandler.respondError(e, ExperimentResultErrors.E_ER_0001);
    }
}
