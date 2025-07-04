package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.support.TaskExecutorAdapter;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Configuration for asynchronous task execution.
 * Optimized for high throughput with virtual threads for better CPU performance.
 */
@Configuration
@EnableAsync
public class AsyncConfig {

    /**
     * Task executor for handling asynchronous operations.
     * Uses Java 21 virtual threads for maximum scalability and CPU efficiency.
     * Virtual threads are lightweight and managed by the JVM, allowing for much higher concurrency.
     */
    @Bean(name = "taskExecutor")
    public Executor taskExecutor() {
        // Create an executor that spawns a new virtual thread for each task
        return new TaskExecutorAdapter(Executors.newVirtualThreadPerTaskExecutor());
    }
}
