package com.uet.agent_simulation_api.pubsub.subcriber.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.uet.agent_simulation_api.pubsub.PubSubCommands;
import com.uet.agent_simulation_api.pubsub.message.master.simulation.DeleteSimulationResult;
import com.uet.agent_simulation_api.pubsub.message.master.simulation.RunSimulation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.stereotype.Service;

/**
 * This class is used to convert a message from a Redis channel to a command and a message data.
 */
@Service
@Slf4j
public class MessageConverterService implements MessageConverter {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Get the node ID.
     *
     * @return The node ID.
     */
    @Override
    public Integer getNodeId(Message message) {
        var nodeId = -1;
        try {
            final var rootNode = objectMapper.readTree(message.toString());
            nodeId = rootNode.get("nodeId").asInt();

            return nodeId;
        } catch (Exception e) {
            log.error("Error while parsing pubsub message", e);

            return null;
        }
    }

    /**
     * Get the command from the message.
     *
     * @param message The message to get the command from.
     * @return The command from the message.
     */
    @Override
    public String getCommand(Message message) {
        var command = "";
        try {
            final var rootNode = objectMapper.readTree(message.toString());
            command = rootNode.get("command").asText();
            if (isEmptyValue(command)) {
                return "";
            }

            return command;
        } catch (Exception e) {
            log.error("Error while parsing pubsub message", e);

            return null;
        }
    }

    /**
     * Check if the value is empty.
     *
     * @param value The value to check.
     * @return True if the value is empty, false otherwise.
     */
    private boolean isEmptyValue(String value) {
        return value == null || value.isBlank() || value.equals("null");
    }

    /**
     * Get the data from the message.
     *
     * @param command The command to get the data from.
     * @param message The message to get the data from.
     * @return The data from the message.
     */
    @Override
    public Object getMessageData(String command, Message message) {
        return switch (command) {
            case PubSubCommands.RUN_SIMULATION -> getRunSimulationData(message);
            case PubSubCommands.DELETE_SIMULATION_RESULT -> getDeleteSimulationResultData(message);
            default -> null;
        };
    }

    /**
     * Get the run simulation data from the message.
     *
     * @param message The message to get the run simulation data from.
     * @return The run simulation data from the message.
     */
    private RunSimulation getRunSimulationData(Message message) {
        RunSimulation data;
        try {
            data = objectMapper.readValue(message.toString(), RunSimulation.class);

            return data;
        } catch (Exception e) {
            log.error("Error while parsing pubsub message", e);

            return null;
        }
    }

    /**
     * Get the delete simulation result data from the message.
     *
     * @param message The message to get the delete simulation result data from.
     * @return The delete simulation result data from the message.
     */
    private DeleteSimulationResult getDeleteSimulationResultData(Message message) {
        DeleteSimulationResult data;
        try {
            data = objectMapper.readValue(message.toString(), DeleteSimulationResult.class);

            return data;
        } catch (Exception e) {
            log.error("Error while parsing pubsub message", e);

            return null;
        }
    }
}
