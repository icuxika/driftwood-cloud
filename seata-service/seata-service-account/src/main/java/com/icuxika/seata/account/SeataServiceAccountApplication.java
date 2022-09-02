package com.icuxika.seata.account;

import com.icuxika.framework.security.annotation.EnableFrameworkResourceServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFrameworkResourceServer
@EnableFeignClients(basePackages = "com.icuxika")
@SpringBootApplication(scanBasePackages = "com.icuxika")
public class SeataServiceAccountApplication {

    public static void main(String[] args) {
        SpringApplication.run(SeataServiceAccountApplication.class, args);
    }

}
