package com.icuxika.service.consumer.controller;

import com.icuxika.framework.basic.common.ApiData;
import com.icuxika.framework.object.modules.user.feign.UserClient;
import com.icuxika.framework.object.modules.user.vo.UserAuthVO;
import com.icuxika.service.consumer.feign.OrderClient;
import com.icuxika.service.consumer.feign.StorageClient;
import io.seata.core.context.RootContext;
import io.seata.spring.annotation.GlobalTransactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("test")
public class TestController {

    private static final Logger L = LoggerFactory.getLogger(TestController.class);

    @Autowired
    private UserClient userClient;

    @Autowired
    private StorageClient storageClient;

    @Autowired
    private OrderClient orderClient;

    @PreAuthorize("hasRole('USER')")
    @GetMapping("printUser")
    public void printUser() {
        ApiData<UserAuthVO> userApiData = userClient.findByUsername("icuxika");
        if (userApiData.isSuccess()) {
            System.out.println(userApiData.getData());
        } else {
            System.out.println("ERROR");
        }
    }

    @GlobalTransactional(timeoutMills = 60000, name = "demo-tx", rollbackFor = Exception.class)
    @GetMapping("seata")
    public String seata() {
        L.info("xid: " + RootContext.getXID());
        storageClient.storage("", 0L);
        orderClient.order(0L, "", 0L);
        return "success";
    }
}
