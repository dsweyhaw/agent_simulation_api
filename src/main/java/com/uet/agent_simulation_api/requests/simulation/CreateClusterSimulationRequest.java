package com.uet.agent_simulation_api.requests.simulation;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateClusterSimulationRequest {
    @Valid
    @NotNull
    private List<CreateSimulationRequest> simulationRequests;
}
