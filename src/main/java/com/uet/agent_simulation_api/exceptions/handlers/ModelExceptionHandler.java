package com.uet.agent_simulation_api.exceptions.handlers;

import com.uet.agent_simulation_api.exceptions.ErrorResponse;
import com.uet.agent_simulation_api.exceptions.errors.ModelErrors;
import com.uet.agent_simulation_api.exceptions.model.ModelNotFoundException;
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
public class ModelExceptionHandler {
    private final ResponseHandler responseHandler;

    /**
     * Handle ModelNotFoundException
     *
     * @param e ModelNotFoundException
     * @return ResponseEntity<ErrorResponse>
     */
    @ExceptionHandler(ModelNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleModelNotFoundException(ModelNotFoundException e) {
        return responseHandler.respondError(e, ModelErrors.E_MODEL_0001);
    }
}
