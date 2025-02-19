package com.acowg.peer.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
public class ScraperConfig {

    @Bean
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(1);  // Adjust based on available CPU cores
        executor.setMaxPoolSize(4);
        executor.setThreadNamePrefix("Scraper-");
        executor.initialize();
        return executor;
    }
}
