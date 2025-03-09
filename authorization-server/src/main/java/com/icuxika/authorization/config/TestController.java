package com.icuxika.authorization.config;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class TestController {

    @GetMapping("test")
    public void test(@RequestHeader Map<String, String> headers) {
        headers.forEach((key, value) -> System.out.println(key + "=" + value));
    }
}
