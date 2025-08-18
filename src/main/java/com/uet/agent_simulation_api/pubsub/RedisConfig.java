package com.uet.agent_simulation_api.pubsub;

import com.uet.agent_simulation_api.pubsub.subcriber.RedisMessageSubscriber;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.GenericToStringSerializer;

@Configuration
@RequiredArgsConstructor
public class RedisConfig {
    private final RedisMessageSubscriber subscriber;

    @Value("${spring.data.redis.host}")
    private String host;

    @Value("${spring.data.redis.port}")
    private int port;

    @Value("${spring.data.redis.password}")
    private String password;

    @Value("${spring.data.redis.database}")
    private int database;

    /**
     * This bean is used to create a Jedis connection factory.
     *
     * @return JedisConnectionFactory
     */
    @Bean
    JedisConnectionFactory jedisConnectionFactory() {
        final var redisStandaloneConfiguration = new RedisStandaloneConfiguration(host, port);
        redisStandaloneConfiguration.setPassword(password);
        redisStandaloneConfiguration.setDatabase(database);

        return new JedisConnectionFactory(redisStandaloneConfiguration);
    }

    /**
     * This bean is used to create a Redis template. It is used to interact with Redis.
     *
     * @return RedisTemplate<String, Object>
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate() {
        final RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(jedisConnectionFactory());
        template.setValueSerializer(new GenericToStringSerializer<>(Object.class));
        template.setEnableTransactionSupport(true);

        return template;
    }

    /**
     * This bean is used to create a pubsub channel.
     *
     * @return ChannelTopic
     */
    @Bean
    ChannelTopic channelTopic() {
        return new ChannelTopic("pubsub_channel");
    }

    /**
     * This bean is used to create a message listener adapter.
     *
     * @return MessageListenerAdapter
     */
    @Bean
    MessageListenerAdapter messageListener() {
        return new MessageListenerAdapter(subscriber);
    }

    /**
     * This bean is used to create a Redis message listener container.
     *
     * @param connectionFactory RedisConnectionFactory
     * @return RedisMessageListenerContainer
     */
    @Bean
    RedisMessageListenerContainer redisContainer(RedisConnectionFactory connectionFactory) {
        final var container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.addMessageListener(messageListener(), channelTopic());

        return container;
    }
}
