package com.icuxika.controller;

import com.icuxika.common.ApiData;
import com.icuxika.user.entity.User;
import com.icuxika.user.feign.UserClient;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("test")
public class TestController {

    private final UserClient userClient;

    public TestController(UserClient userClient) {
        this.userClient = userClient;
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("printUser")
    public void printUser() {
        ApiData<User> userApiData = userClient.findByUsername("icuxika");
        if (userApiData.isSuccess()) {
            System.out.println(userApiData.getData());
        } else {
            System.out.println("ERROR");
        }
    }
}
