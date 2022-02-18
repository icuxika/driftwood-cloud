package com.icuxika.user.feign;

import com.icuxika.common.ApiData;
import com.icuxika.user.entity.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

public interface UserFeignApi {

    @GetMapping(value = "/user/findByUsername", headers = {"FEIGN-REQUEST=VERIFICATION"})
    ApiData<User> findByUsername(@RequestParam("username") String username);

    @GetMapping(value = "/user/findByPhone", headers = {"FEIGN-REQUEST=VERIFICATION"})
    ApiData<User> findByPhone(@RequestParam("phone") String phone);
}
