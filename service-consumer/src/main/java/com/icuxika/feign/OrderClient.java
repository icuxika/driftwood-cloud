package com.icuxika.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "seata-service-order")
public interface OrderClient {

    @GetMapping("/order")
    String order(@RequestParam("userId") Long userId, @RequestParam("commodityCode") String commodityCode, @RequestParam("orderCount") Long orderCount);
}
