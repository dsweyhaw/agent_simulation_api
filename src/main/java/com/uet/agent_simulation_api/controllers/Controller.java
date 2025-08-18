package com.uet.agent_simulation_api.controllers;

import com.uet.agent_simulation_api.responses.ResponseHandler;
import com.uet.agent_simulation_api.responses.SuccessResponse;
import com.uet.agent_simulation_api.services.node.INodeService;
import com.uet.agent_simulation_api.utils.ThreadUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Slf4j
public class Controller {
    private final ThreadUtil threadUtil;
    private final INodeService nodeService;
    private final ResponseHandler responseHandler;

    @Value("${server.port}")
    private int port;

    @Value("${spring.profiles.active}")
    private String profile;

    @GetMapping("/health")
    public ResponseEntity<SuccessResponse> health() {
       return responseHandler.respondSuccess("OK");
    }

    @GetMapping("/current_node")
    public ResponseEntity<SuccessResponse> getCurrentNode() {
        return responseHandler.respondSuccess(nodeService.getCurrentNode());
    }

    @DeleteMapping("/threads/{id}")
    public ResponseEntity<SuccessResponse> killThread(@PathVariable long id) {
        threadUtil.showAll();
        threadUtil.killThread(id);

        return responseHandler.respondSuccess("OK");
    }

    @GetMapping("/threads")
    public ResponseEntity<SuccessResponse> showAllThreads() {
        threadUtil.showAll();

        return responseHandler.respondSuccess("OK");
    }

    @DeleteMapping("/processes/{pid}")
    public ResponseEntity<SuccessResponse> killProcess(@PathVariable long pid) {
        threadUtil.killProcessById(pid);

        return responseHandler.respondSuccess("OK");
    }
}
