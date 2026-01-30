package org.codenot.ssa.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class AsyncConfig {
    @Value("${assessment.executor.corePoolSize}")
    private int corePoolSize;

    @Value("${assessment.executor.maxPoolSize}")
    private int maxPoolSize;

    @Value("${assessment.executor.queueCapacity}")
    private int queueCapacity;

    @Value("${assessment.executor.threadNamePrefix}")
    private String threadNamePrefix;

    @Bean(name = "assessmentExecutor")
    public Executor assessmentExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setThreadNamePrefix(threadNamePrefix);
        executor.initialize();
        return executor;
    }
}
