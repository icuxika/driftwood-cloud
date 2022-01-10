package com.icuxika.controller;

import com.icuxika.common.ApiData;
import com.icuxika.user.entity.User;
import com.icuxika.user.feign.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("test")
public class TestController {

    private final UserService userService;

    public TestController(UserService userService) {
        this.userService = userService;
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("printUser")
    public void printUser() {
        ApiData<User> userApiData = userService.findByUsername("icuxika");
        if (userApiData.isSuccess()) {
            System.out.println(userApiData.getData());
        } else {
            System.out.println("ERROR");
        }
    }
}
