package com.icuxika;

import com.icuxika.annotations.EnableFrameworkResourceServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableFrameworkResourceServer
@SpringBootApplication
public class FrameworkServiceWebsocketApplication {

    public static void main(String[] args) {
        SpringApplication.run(FrameworkServiceWebsocketApplication.class, args);
    }

}
