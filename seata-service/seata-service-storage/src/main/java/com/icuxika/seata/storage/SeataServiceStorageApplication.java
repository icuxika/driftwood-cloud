package com.icuxika.seata.storage;

import com.icuxika.framework.security.annotation.EnableFrameworkResourceServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@EnableFrameworkResourceServer
@SpringBootApplication
public class SeataServiceStorageApplication {

    public static void main(String[] args) {
        SpringApplication.run(SeataServiceStorageApplication.class, args);
    }

}
