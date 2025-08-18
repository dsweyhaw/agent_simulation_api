package com.uet.agent_simulation_api.exceptions.handlers;

import com.uet.agent_simulation_api.exceptions.ErrorResponse;
import com.uet.agent_simulation_api.exceptions.errors.NodeErrors;
import com.uet.agent_simulation_api.exceptions.node.CannotConnectToNodeException;
import com.uet.agent_simulation_api.exceptions.node.CannotFetchNodeDataException;
import com.uet.agent_simulation_api.exceptions.node.NodeNotFoundException;
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
public class NodeExceptionHandler {
    private final ResponseHandler responseHandler;

    /**
     * Handle NodeNotFoundException
     *
     * @param e NodeNotFoundException
     * @return ResponseEntity<ErrorResponse>
     */
    @ExceptionHandler(NodeNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNodeNotFoundException(NodeNotFoundException e) {
        return responseHandler.respondError(e, NodeErrors.E_NODE_0001);
    }

    /**
     * Handle CannotConnectToNodeException
     *
     * @param e CannotConnectToNodeException
     * @return ResponseEntity<ErrorResponse>
     */
    @ExceptionHandler(CannotConnectToNodeException.class)
    public ResponseEntity<ErrorResponse> handleCannotConnectToNodeException(CannotConnectToNodeException e) {
        return responseHandler.respondError(e, NodeErrors.E_NODE_0002);
    }

    /**
     * Handle CannotFetchNodeDataException
     *
     * @param e CannotFetchNodeDataException
     * @return ResponseEntity<ErrorResponse>
     */
    @ExceptionHandler(CannotFetchNodeDataException.class)
    public ResponseEntity<ErrorResponse> handleCannotFetchNodeDataException(CannotFetchNodeDataException e) {
        return responseHandler.respondError(e, NodeErrors.E_NODE_0003);
    }
}
