package com.uet.agent_simulation_api.pubsub.subcriber.services;

import com.uet.agent_simulation_api.pubsub.PubSubCommands;
import com.uet.agent_simulation_api.pubsub.message.master.simulation.DeleteSimulationResult;
import com.uet.agent_simulation_api.pubsub.message.master.simulation.RunSimulation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MessageRouterService implements MessageRouter {
    private final MessageHandler messageHandler;

    /**
     * Handle the message.
     *
     * @param command The command.
     * @param messageData The message data.
     */
    public void handle(String command, Object messageData) {
        switch (command) {
            case PubSubCommands.RUN_SIMULATION:
                messageHandler.runSimulation((RunSimulation) messageData);
                break;

            case PubSubCommands.DELETE_SIMULATION_RESULT:
                messageHandler.deleteSimulationResult((DeleteSimulationResult) messageData);
                break;
        }
    }
}
