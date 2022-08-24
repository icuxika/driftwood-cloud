package com.icuxika.async;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class AsyncConfig {

    private static final Logger L = LoggerFactory.getLogger(AsyncConfig.class);

    @Bean
    @ConditionalOnExpression("!'${spring.application.name}'.equals('framework-service-flowable')")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(24);
        executor.setMaxPoolSize(32);
        executor.setQueueCapacity(500);
        executor.setKeepAliveSeconds(60);
        executor.setThreadNamePrefix("DriftwoodAsyncExecutor-");
        executor.setRejectedExecutionHandler((r, e) -> L.warn("任务[" + r.toString() + "]被[" + e.toString() + "]拒绝"));
        executor.initialize();
        return executor;
    }
}
