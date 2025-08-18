package com.uet.agent_simulation_api.responses.auth;

import java.math.BigInteger;

public record GetMeResponse(
    BigInteger id,
    String fullname,
    String email,
    Integer role
) {}
