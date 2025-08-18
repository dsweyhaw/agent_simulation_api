package com.uet.agent_simulation_api.responses.auth;

public record RefreshResponse (
        String accessToken,
        String refreshToken
) {}
