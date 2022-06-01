package com.icuxika.async;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class AsyncWrapper {

    private static final Logger L = LoggerFactory.getLogger(AsyncWrapper.class);

    @Async
    public void doAsync(String name, Runnable task) {
        L.info("[ASYNC][" + name + "]" + Thread.currentThread().getName());
        task.run();
    }
}
