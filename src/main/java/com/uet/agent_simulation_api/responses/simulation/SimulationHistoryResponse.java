package com.uet.agent_simulation_api.responses.simulation;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;

public record SimulationHistoryResponse (
    BigInteger id,
    String resultIds, // concat of experimentResultId separated by comma
    Date createdAt,
    List<SimulationDetailResponse> details
) {}
