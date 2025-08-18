package com.uet.agent_simulation_api.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * This class is used to configure threads.
 */
@Configuration
public class ThreadConfig {
    /*
     * This bean is used to create a virtual thread executor to handle multiple tasks concurrently.
     */
    @Bean
    public ExecutorService virtualThreadExecutor() {
        return Executors.newVirtualThreadPerTaskExecutor();
    }
}
