package com.uet.agent_simulation_api.pubsub.publisher;

import com.uet.agent_simulation_api.pubsub.message.master.MasterMessage;

/**
 * This interface is used to publish a message to a Redis channel.
 */
public interface MessagePublisher {
    void publish(MasterMessage message);
}
