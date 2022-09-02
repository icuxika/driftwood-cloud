package com.icuxika.service.consumer.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "seata-service-storage")
public interface StorageClient {

    @GetMapping("/storageAT")
    String storageAT(@RequestParam("commodityCode") String commodityCode, @RequestParam("count") Long count);
}
