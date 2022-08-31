package com.icuxika.seata.order.controller;

import com.icuxika.seata.order.entity.Order;
import com.icuxika.seata.order.feign.AccountClient;
import com.icuxika.seata.order.repository.OrderRepository;
import io.seata.core.context.RootContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
public class OrderController {

    private static final Logger L = LoggerFactory.getLogger(OrderController.class);

    @Autowired
    private AccountClient accountClient;

    @Autowired
    private OrderRepository orderRepository;

    @GetMapping("/order")
    public String order(Long userId, String commodityCode, Long orderCount) {
        L.info("xid: " + RootContext.getXID());
        if (userId == 0L) {
            throw new RuntimeException("aa");
        }
        Order order = new Order();
        order.setCreateTime(LocalDateTime.now());
        order.setCreateUserId(0L);
        order.setUpdateTime(LocalDateTime.now());
        order.setUpdateUserId(0L);
        order.setUserId(0L);
        order.setCommodityCode("code");
        order.setCount(1L);
        order.setMoney(1L);
        orderRepository.save(order);
        String result = accountClient.account(0L, 0L);
        System.out.println(result);
        return "success";
    }
}
