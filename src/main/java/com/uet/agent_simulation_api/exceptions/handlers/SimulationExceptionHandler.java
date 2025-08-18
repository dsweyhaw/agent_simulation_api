package com.uet.agent_simulation_api.exceptions.handlers;

import com.uet.agent_simulation_api.exceptions.ErrorResponse;
import com.uet.agent_simulation_api.exceptions.errors.SimulationErrors;
import com.uet.agent_simulation_api.exceptions.simulation.CannotClearOldSimulationOutputException;
import com.uet.agent_simulation_api.exceptions.simulation.SimulationRunNotFoundException;
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
public class SimulationExceptionHandler {
    private final ResponseHandler responseHandler;

    /**
     * Handle CannotClearOldSimulationOutputException
     *
     * @param e CannotClearOldSimulationOutputException
     * @return ResponseEntity<ErrorResponse>
     */
    @ExceptionHandler(CannotClearOldSimulationOutputException.class)
    public ResponseEntity<ErrorResponse> handleCannotClearOldSimulationOutputException(CannotClearOldSimulationOutputException e) {
        return responseHandler.respondError(e, SimulationErrors.E_SM_0001);
    }

    /**
     * Handle SimulationRunNotFoundException
     *
     * @param e SimulationRunNotFoundException
     * @return ResponseEntity<ErrorResponse>
     */
    @ExceptionHandler(SimulationRunNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleSimulationRunNotFoundException(SimulationRunNotFoundException e) {
        return responseHandler.respondError(e, SimulationErrors.E_SM_0002);
    }
}
