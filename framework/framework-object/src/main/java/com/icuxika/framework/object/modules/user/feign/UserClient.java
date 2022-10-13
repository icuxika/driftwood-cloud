package com.icuxika.framework.object.modules.user.feign;

import com.icuxika.framework.basic.common.ApiData;
import com.icuxika.framework.object.modules.user.vo.UserAuthVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "user-service", contextId = "userClient", fallbackFactory = UserClientFallbackFactory.class)
public interface UserClient {

    @GetMapping(value = "/user/findByUsername")
    ApiData<UserAuthVO> findByUsername(@RequestParam("username") String username);

    @GetMapping(value = "/user/findByPhone")
    ApiData<UserAuthVO> findByPhone(@RequestParam("phone") String phone);

    @GetMapping("/user/findByOpenid")
    ApiData<UserAuthVO> findByOpenid(@RequestParam("openid") String openid, @RequestParam("type") Integer type);

    @GetMapping("/user/findThirdBindByOpenid")
    ApiData<Boolean> findThirdBindByOpenid(@RequestParam("openid") String openid, @RequestParam("type") Integer type);

    @PostMapping("/user/updateUserIP")
    ApiData<Void> updateUserIP(@RequestParam("userId") Long userId, @RequestParam("ip") String ip);
}
