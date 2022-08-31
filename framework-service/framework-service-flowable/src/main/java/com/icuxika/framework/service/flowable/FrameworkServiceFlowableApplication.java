package com.icuxika.framework.service.flowable;

import com.icuxika.framework.security.annotation.EnableFrameworkResourceServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableFrameworkResourceServer
@SpringBootApplication(scanBasePackages = "com.icuxika")
public class FrameworkServiceFlowableApplication {

    public static void main(String[] args) {
        SpringApplication.run(FrameworkServiceFlowableApplication.class, args);
    }

}
