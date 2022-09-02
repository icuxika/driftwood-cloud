package com.icuxika.service.consumer.controller;

import com.icuxika.service.consumer.feign.OrderClient;
import com.icuxika.service.consumer.feign.StorageClient;
import io.seata.core.context.RootContext;
import io.seata.spring.annotation.GlobalTransactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Seata 测试
 */
@RestController
@RequestMapping("seata")
public class SeataController {

    private static final Logger L = LoggerFactory.getLogger(SeataController.class);

    @Autowired
    private StorageClient storageClient;

    @Autowired
    private OrderClient orderClient;

    /**
     * Seata AT 模式
     */
    @GlobalTransactional(timeoutMills = 60000, name = "demo-tx-at", rollbackFor = Exception.class)
    @GetMapping("startAT")
    public String startAT() {
        L.info("xid: " + RootContext.getXID());
        storageClient.storageAT("", 0L);
        orderClient.orderAT(0L, "", 0L);
        return "success";
    }

    /**
     * Seata TCC 模式
     */
    public void startTCC() {
    }

    /**
     * SEATA Saga 模式
     */
    public void startSaga() {
    }

    /**
     * Seata XA 模式
     */
    public void startXA() {
    }

}
