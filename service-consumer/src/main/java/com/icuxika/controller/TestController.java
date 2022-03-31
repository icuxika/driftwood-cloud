package com.icuxika.controller;

import com.icuxika.common.ApiData;
import com.icuxika.feign.OrderClient;
import com.icuxika.feign.StorageClient;
import com.icuxika.user.feign.UserClient;
import com.icuxika.user.vo.UserAuthVO;
import io.seata.core.context.RootContext;
import io.seata.spring.annotation.GlobalTransactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("test")
public class TestController {

    private static final Logger L = LoggerFactory.getLogger(TestController.class);

    @Autowired
    @Qualifier("com.icuxika.user.feign.UserClient")
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
