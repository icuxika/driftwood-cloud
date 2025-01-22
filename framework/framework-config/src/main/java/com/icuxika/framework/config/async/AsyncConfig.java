package com.icuxika.framework.config.async;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
@Slf4j
public class AsyncConfig {

    @Bean
    @ConditionalOnExpression("!'${spring.application.name}'.equals('framework-service-flowable') && !'${spring.application.name}'.equals('framework-service-activiti')")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(24);
        executor.setMaxPoolSize(32);
        executor.setQueueCapacity(500);
        executor.setKeepAliveSeconds(60);
        executor.setThreadNamePrefix("DriftwoodAsyncExecutor-");
        executor.setRejectedExecutionHandler((r, e) -> {
            if (log.isWarnEnabled()) {
                log.warn("任务[{}]被[{}]拒绝", r.toString(), e.toString());
            }
        });
        executor.initialize();
        return executor;
    }
}
