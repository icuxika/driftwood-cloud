package com.icuxika.seata.account.controller;

import io.seata.core.context.RootContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AccountController {

    private static final Logger L = LoggerFactory.getLogger(AccountController.class);

    @GetMapping("/account")
    public String account(Long userId, Long money) {
        L.info("xid: " + RootContext.getXID());
        if (userId == 0) {
            throw new RuntimeException("xxxx");
        }
        return "success";
    }
}
