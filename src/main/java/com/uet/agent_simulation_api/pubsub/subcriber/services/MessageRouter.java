package com.uet.agent_simulation_api.pubsub.subcriber.services;

public interface MessageRouter {
    void handle(String command, Object messageData);
}
