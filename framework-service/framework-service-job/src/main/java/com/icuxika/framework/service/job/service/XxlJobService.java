package com.icuxika.framework.service.job.service;

import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class XxlJobService {

    /**
     * 1、简单任务示例（Bean模式）
     */
    @XxlJob("demoJobHandler")
    public void demoJobHandler() throws Exception {
        if (log.isInfoEnabled()) {
            log.info("XXL-JOB, Hello World.");
        }

        for (int i = 0; i < 5; i++) {
            if (log.isInfoEnabled()) {
                log.info("beat at:{}", i);
            }
            TimeUnit.SECONDS.sleep(2);
        }
    }

}
