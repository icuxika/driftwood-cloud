package com.icuxika;

import com.icuxika.annotations.EnableFrameworkResourceServer;
import com.icuxika.annotations.EnableFrameworkXxlJob;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableFrameworkXxlJob
@EnableFrameworkResourceServer
@SpringBootApplication
public class FrameworkServiceJobApplication {

    public static void main(String[] args) {
        SpringApplication.run(FrameworkServiceJobApplication.class, args);
    }

}
