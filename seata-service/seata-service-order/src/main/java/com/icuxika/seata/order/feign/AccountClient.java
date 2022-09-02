package com.icuxika.seata.order.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "seata-service-account")
public interface AccountClient {

    @GetMapping("/accountAT")
    String accountAT(@RequestParam("userId") Long userId, @RequestParam("money") Long money);

}
