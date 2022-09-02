package com.icuxika.service.consumer.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "seata-service-order")
public interface OrderClient {

    @GetMapping("/orderAT")
    String orderAT(@RequestParam("userId") Long userId, @RequestParam("commodityCode") String commodityCode, @RequestParam("orderCount") Long orderCount);
}
