package com.icuxika.framework.service.websocket.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class SessionDeathDetectionRunner implements ApplicationRunner {

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    @Override
    public void run(ApplicationArguments args) {
        if (log.isInfoEnabled()) {
            log.info("开始会话死亡定时检测任务");
        }
        scheduler.scheduleWithFixedDelay(() -> {
            List<ManageableWebSocketSession> userSessionList = WebSocketSessionManager.getCurrentWebSocketSessionList();
            if (userSessionList.isEmpty()) {
                if (log.isWarnEnabled()) {
                    log.warn("没有用户在线");
                }
            } else {
                userSessionList.forEach(manageableWebSocketSession -> {
                    if (!manageableWebSocketSession.sendPing()) {
                        WebSocketSessionManager.closeSession(manageableWebSocketSession.getUserId(), manageableWebSocketSession.getWebSocketSession());
                        if (log.isInfoEnabled()) {
                            log.info("{}超时下线", manageableWebSocketSession.getUserId());
                        }
                    }
                });
            }
        }, 0, 5, TimeUnit.SECONDS);
    }
}
