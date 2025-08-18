package com.uet.agent_simulation_api.pubsub.subcriber;

import com.uet.agent_simulation_api.pubsub.subcriber.services.MessageConverter;
import com.uet.agent_simulation_api.pubsub.subcriber.services.MessageRouter;
import com.uet.agent_simulation_api.utils.FileUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Service;

/**
 * This class is used to subscribe to a Redis channel.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class RedisMessageSubscriber implements MessageListener {
    private final FileUtil fileUtil;
    private final MessageRouter router;
    private final MessageConverter messageConverter;

    @Value("${cluster.config.path}")
    private String clusterConfigPath;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        final var messageNodeId = messageConverter.getNodeId(message);
        if (messageNodeId == null) {
            log.error("Invalid message node ID");
            return;
        }
        if (messageNodeId != getNodeId()) {
            return;
        }

        final var command = messageConverter.getCommand(message);
        if (command == null || command.isEmpty()) {
            log.error("Unknown command");
            return;
        }

        final var messageData = messageConverter.getMessageData(command, message);
        if (messageData == null) {
            log.error("Invalid message data");
        }

        router.handle(command, messageData);
    }

    private int getNodeId() {
        return Integer.parseInt(fileUtil.getValueByKey(clusterConfigPath, "node_id"));
    }
}
