package com.icuxika.framework.service.job;

import com.icuxika.framework.security.annotation.EnableFrameworkResourceServer;
import com.icuxika.framework.xxl.job.annotation.EnableFrameworkXxlJob;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableFrameworkXxlJob
@EnableFrameworkResourceServer
@SpringBootApplication(scanBasePackages = "com.icuxika")
public class FrameworkServiceJobApplication {

    public static void main(String[] args) {
        SpringApplication.run(FrameworkServiceJobApplication.class, args);
    }

}
