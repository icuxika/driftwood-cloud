package com.icuxika.user.feign;

import com.icuxika.common.ApiData;
import com.icuxika.user.entity.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "user-service", contextId = "userClient", fallback = UserClientFallback.class)
public interface UserClient {

    @GetMapping(value = "/user/findByUsername")
    ApiData<User> findByUsername(@RequestParam("username") String username);

    @GetMapping(value = "/user/findByPhone")
    ApiData<User> findByPhone(@RequestParam("phone") String phone);

}
