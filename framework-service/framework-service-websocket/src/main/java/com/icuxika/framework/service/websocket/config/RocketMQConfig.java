package com.icuxika.framework.service.websocket.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@Component
@Slf4j
public class RocketMQConfig {

    @Bean
    public Consumer<Message<SimpleMessage>> websocketMessageConsumer() {
        return s -> {
            if (log.isInfoEnabled()) {
                SimpleMessage simpleMessage = s.getPayload();
                log.info("Receive message: {}", simpleMessage);
                WebSocketSessionManager.sendMessageToUser(simpleMessage.getUserId(), "response: " + simpleMessage.getMessage());
            }
        };
    }

}
