package ch.uzh.ifi.hase.soprafs24.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurerSupport;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
public class AsyncConfig extends AsyncConfigurerSupport {

    @Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5); // Base threads - always maintained
        executor.setMaxPoolSize(10); // Limit for thread (any threads beyond CorePool need to be initialized, leading to delays)
        executor.setQueueCapacity(50); // Limit for tasks that can be queued for execution by one of the threads
        executor.setThreadNamePrefix("GameExecutor-");
        executor.initialize();
        return executor;
    }
}