package com.uet.agent_simulation_api.exceptions.handlers;

import com.uet.agent_simulation_api.exceptions.ErrorResponse;
import com.uet.agent_simulation_api.exceptions.errors.ExperimentResultImageErrors;
import com.uet.agent_simulation_api.exceptions.experiment_result_image.ExperimentResultImageDataReadException;
import com.uet.agent_simulation_api.exceptions.experiment_result_image.ExperimentResultImageNotFoundException;
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
public class ExperimentResultImageExceptionHandler {
    private final ResponseHandler responseHandler;

    /**
     * Handle ExperimentResultImageNotFoundException
     *
     * @param e ExperimentResultImageNotFoundException
     * @return ResponseEntity<ErrorResponse>
     */
    @ExceptionHandler(ExperimentResultImageNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleExperimentResultImageNotFoundException(ExperimentResultImageNotFoundException e) {
        return responseHandler.respondError(e, ExperimentResultImageErrors.E_ERI_0001);
    }

    /**
     * Handle ExperimentResultImageDataReadException
     *
     * @param e ExperimentResultImageDataReadException
     * @return ResponseEntity<ErrorResponse>
     */
    @ExceptionHandler(ExperimentResultImageDataReadException.class)
    public ResponseEntity<ErrorResponse> handleExperimentResultImageDataReadException(ExperimentResultImageDataReadException e) {
        return responseHandler.respondError(e, ExperimentResultImageErrors.E_ERI_0002);
    }
}
