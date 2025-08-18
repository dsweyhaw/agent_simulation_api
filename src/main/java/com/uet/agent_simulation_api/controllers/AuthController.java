package com.uet.agent_simulation_api.controllers;

import com.uet.agent_simulation_api.requests.auth.LoginRequest;
import com.uet.agent_simulation_api.requests.auth.RefreshRequest;
import com.uet.agent_simulation_api.requests.auth.RegisterRequest;
import com.uet.agent_simulation_api.responses.ResponseHandler;
import com.uet.agent_simulation_api.responses.SuccessResponse;
import com.uet.agent_simulation_api.services.auth.IAuthService;
import com.uet.agent_simulation_api.utils.TrimUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final TrimUtil trimUtil;
    private final IAuthService authService;
    private final ResponseHandler responseHandler;

    @PostMapping("/login")
    public ResponseEntity<SuccessResponse> login(@Valid @RequestBody LoginRequest request) {
        trimUtil.trimFields(request);

        return responseHandler.respondSuccess(authService.login(request));
    }

    @PostMapping("/register")
    public ResponseEntity<SuccessResponse> register(@Valid @RequestBody RegisterRequest request) {
        trimUtil.trimFields(request);

        return responseHandler.respondSuccess(HttpStatus.CREATED, authService.register(request));
    }

    @GetMapping("/me")
    public ResponseEntity<SuccessResponse> getMe() {
        return responseHandler.respondSuccess(authService.getMe());
    }

    @PostMapping("/refresh")
    public ResponseEntity<SuccessResponse> refresh(@Valid @RequestBody RefreshRequest request) {
        trimUtil.trimFields(request);

        return responseHandler.respondSuccess(HttpStatus.CREATED, authService.refresh(request));
    }
}
