package com.uet.agent_simulation_api.pubsub.subcriber.services;

import org.springframework.data.redis.connection.Message;

/**
 * This interface is used to convert a message from a Redis channel to a command and a message data.
 */
public interface MessageConverter {
    Integer getNodeId(Message message);
    String getCommand(Message message);
    Object getMessageData(String command, Message message);
}
