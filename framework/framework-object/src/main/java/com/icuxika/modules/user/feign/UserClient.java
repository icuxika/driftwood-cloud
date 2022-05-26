package com.icuxika.modules.user.feign;

import com.icuxika.common.ApiData;
import com.icuxika.modules.user.vo.UserAuthVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "user-service", contextId = "userClient", fallbackFactory = UserClientFallbackFactory.class)
public interface UserClient {

    @GetMapping(value = "/user/findByUsername")
    ApiData<UserAuthVO> findByUsername(@RequestParam("username") String username);

    @GetMapping(value = "/user/findByPhone")
    ApiData<UserAuthVO> findByPhone(@RequestParam("phone") String phone);

}
