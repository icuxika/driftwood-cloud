package com.icuxika.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "seata-service-account")
public interface AccountClient {

    @GetMapping("/account")
    String account(@RequestParam("userId") Long userId, @RequestParam("money") Long money);
}
