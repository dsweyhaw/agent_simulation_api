package com.uet.agent_simulation_api.pubsub.message.master.simulation;

import com.uet.agent_simulation_api.pubsub.message.master.MasterMessage;
import com.uet.agent_simulation_api.requests.simulation.CreateSimulationRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class RunSimulation extends MasterMessage {
    private CreateSimulationRequest simulation;
}
