package com.uet.agent_simulation_api.requests.simulation;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

/**
 * Create simulation request class.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateSimulationRequest {
    private Integer nodeId;

    private BigInteger projectId;

    private String projectLocation;

    private Integer order;

    private Integer number;

    private Map<String, String> gamaParams;

    @Valid
    @NotNull
    private List<CreateExperimentRequest> experiments;
}
