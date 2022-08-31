package com.icuxika.framework.service.minio;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@EntityScan(basePackages = "com.icuxika")
@SpringBootApplication(scanBasePackages = "com.icuxika")
public class FrameworkServiceMinioApplication {

    public static void main(String[] args) {
        SpringApplication.run(FrameworkServiceMinioApplication.class, args);
    }

}
