package com.icuxika.framework.service.job.service;

import com.xxl.job.core.handler.annotation.XxlJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class XxlJobService {

    private final Logger L = LoggerFactory.getLogger(XxlJobService.class);

    /**
     * 1、简单任务示例（Bean模式）
     */
    @XxlJob("demoJobHandler")
    public void demoJobHandler() throws Exception {
        if (L.isInfoEnabled()) {
            L.info("XXL-JOB, Hello World.");
        }

        for (int i = 0; i < 5; i++) {
            if (L.isInfoEnabled()) {
                L.info("beat at:" + i);
            }
            TimeUnit.SECONDS.sleep(2);
        }
    }

}
