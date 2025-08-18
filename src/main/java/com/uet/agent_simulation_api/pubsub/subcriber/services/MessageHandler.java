package com.uet.agent_simulation_api.pubsub.subcriber.services;

import com.uet.agent_simulation_api.pubsub.message.master.simulation.DeleteSimulationResult;
import com.uet.agent_simulation_api.pubsub.message.master.simulation.RunSimulation;

public interface MessageHandler {
    /**
     * Worker run simulation.
     *
     * @param messageData The message data.
     */
    void runSimulation(RunSimulation messageData);

    /**
     * Worker delete simulation result.
     *
     * @param messageData The message data.
     */
    void deleteSimulationResult(DeleteSimulationResult messageData);
}
