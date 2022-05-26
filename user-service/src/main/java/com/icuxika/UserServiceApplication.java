package com.icuxika;

import com.icuxika.annotations.EnableFrameworkJpaAuditing;
import com.icuxika.annotations.EnableFrameworkResourceServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFrameworkJpaAuditing
@EnableFeignClients
@EnableFrameworkResourceServer
@SpringBootApplication
public class UserServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserServiceApplication.class, args);
    }

}
