package com.uet.agent_simulation_api.responses.node;

public record NodeBriefResponse (
    Integer id,
    String name,
    Integer role
) {}
