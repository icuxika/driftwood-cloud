package com.icuxika.seata.storage.controller;

import com.icuxika.seata.storage.entity.Storage;
import com.icuxika.seata.storage.repository.StorageRepository;
import io.seata.core.context.RootContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
public class StorageController {

    private static final Logger L = LoggerFactory.getLogger(StorageController.class);

    @Autowired
    private StorageRepository storageRepository;

    @GetMapping("/storageAT")
    public String storageAT(String commodityCode, Long count) {
        L.info("xid: " + RootContext.getXID());
        Storage storage = new Storage();
        storage.setCreateTime(LocalDateTime.now());
        storage.setCreateUserId(0L);
        storage.setUpdateTime(LocalDateTime.now());
        storage.setUpdateUserId(0L);
        storage.setCommodityCode("code");
        storage.setCount(1L);
        storageRepository.save(storage);
        return "success";
    }
}
