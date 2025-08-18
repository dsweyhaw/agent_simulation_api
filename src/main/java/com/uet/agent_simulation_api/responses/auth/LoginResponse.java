package com.uet.agent_simulation_api.responses.auth;

public record LoginResponse(
        String accessToken,
        String refreshToken
) {}
