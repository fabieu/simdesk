package de.sustineo.simdesk.configuration;

import com.vaadin.flow.spring.annotation.VaadinTaskExecutor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class AsyncConfiguration {

    /**
     * This represents essentially a fixed thread pool executor with additional configuration
     * @return Default executor for @Async
     */
    @Bean
    @VaadinTaskExecutor
    public Executor taskExecutor() {
        int poolSize = Runtime.getRuntime().availableProcessors() * 4;

        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(poolSize);
        executor.setMaxPoolSize(poolSize);
        executor.setThreadNamePrefix("task-");
        executor.setAwaitTerminationSeconds(5);
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.initialize();
        return executor;
    }
}
