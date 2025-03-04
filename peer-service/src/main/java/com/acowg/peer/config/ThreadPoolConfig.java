package com.acowg.peer.config;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

@Configuration
public class ThreadPoolConfig {

    @Bean
    @Primary
    public Executor applicationExecutor() {
        ThreadFactory threadFactory = new ThreadFactoryBuilder()
                .setNameFormat("app-pool-%d")
                .setDaemon(true)
                .build();
                
        ScheduledThreadPoolExecutor executor = (ScheduledThreadPoolExecutor) 
                Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors(), threadFactory);
        
        // Optional configurations
        executor.setRemoveOnCancelPolicy(true);
        executor.setExecuteExistingDelayedTasksAfterShutdownPolicy(false);
        
        return executor;
    }
}