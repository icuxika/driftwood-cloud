package com.icuxika.framework.config.async;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class AsyncWrapper {

    @Async
    public void doAsync(String name, Runnable task) {
        if (log.isInfoEnabled()) {
            log.info("[ASYNC][{}]{}", name, Thread.currentThread().getName());
        }
        task.run();
    }
}
