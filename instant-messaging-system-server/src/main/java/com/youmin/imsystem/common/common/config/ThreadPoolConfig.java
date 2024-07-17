package com.youmin.imsystem.common.common.config;

import com.youmin.imsystem.common.common.factory.MyThreadFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Thread Pool Configuration
 */
@Configuration
@EnableAsync
public class ThreadPoolConfig implements AsyncConfigurer {
    /**
     * Project's share thread pool
     */
    private static final String IMSYSTEM_EXECUTOR = "IMSystemExecutor";

    @Override
    public Executor getAsyncExecutor() {
        return null;
    }

    @Bean(name = IMSYSTEM_EXECUTOR)
    public ThreadPoolTaskExecutor imSystemExecutor(){
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setMaxPoolSize(10);
        executor.setCorePoolSize(10);
        executor.setThreadNamePrefix(IMSYSTEM_EXECUTOR);
        executor.setQueueCapacity(200);
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.setThreadFactory(new MyThreadFactory(executor));
        executor.initialize();
        return executor;
    }

}
