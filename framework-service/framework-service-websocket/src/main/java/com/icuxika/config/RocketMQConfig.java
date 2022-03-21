package com.icuxika.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@Component
public class RocketMQConfig {

    private static final Logger L = LoggerFactory.getLogger(RocketMQConfig.class);

    @Bean
    public Consumer<String> websocketMessageConsumer() {
        return new Consumer<String>() {
            @Override
            public void accept(String s) {
                L.info("Receive message: " + s);
            }
        };
    }
}
