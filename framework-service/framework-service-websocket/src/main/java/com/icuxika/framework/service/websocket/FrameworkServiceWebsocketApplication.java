package com.icuxika.framework.service.websocket;

import com.icuxika.framework.security.annotation.EnableFrameworkResourceServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableFrameworkResourceServer
@SpringBootApplication(scanBasePackages = "com.icuxika")
public class FrameworkServiceWebsocketApplication {

    public static void main(String[] args) {
        SpringApplication.run(FrameworkServiceWebsocketApplication.class, args);
    }

}
