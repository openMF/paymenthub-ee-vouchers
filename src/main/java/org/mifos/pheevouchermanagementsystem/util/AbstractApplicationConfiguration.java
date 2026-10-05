package org.mifos.pheevouchermanagementsystem.util;

import java.util.concurrent.Executor;
import org.mifos.pheevouchermanagementsystem.config.AsyncProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableAsync
public class AbstractApplicationConfiguration {

    private final AsyncProperties asyncProperties;

    public AbstractApplicationConfiguration(AsyncProperties asyncProperties) {
        this.asyncProperties = asyncProperties;
    }

    @Bean(name = "asyncExecutor")
    public Executor asyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(asyncProperties.corePoolSize());
        executor.setMaxPoolSize(asyncProperties.maxPoolSize());
        executor.setQueueCapacity(asyncProperties.queueCapacity());
        executor.setThreadNamePrefix("AsyncThread-");
        executor.initialize();
        return executor;
    }
}
