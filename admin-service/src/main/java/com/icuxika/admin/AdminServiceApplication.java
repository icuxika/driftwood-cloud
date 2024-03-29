package com.icuxika.admin;

import com.icuxika.framework.config.jpa.annotation.EnableFrameworkJpaAuditing;
import com.icuxika.framework.security.annotation.EnableFrameworkResourceServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFrameworkJpaAuditing
@EnableFrameworkResourceServer
@EntityScan(basePackages = "com.icuxika")
@EnableFeignClients(basePackages = "com.icuxika")
@SpringBootApplication(scanBasePackages = "com.icuxika")
public class AdminServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AdminServiceApplication.class, args);
    }

}
