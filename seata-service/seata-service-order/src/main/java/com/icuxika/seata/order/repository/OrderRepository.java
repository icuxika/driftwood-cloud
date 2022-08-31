package com.icuxika.seata.order.repository;

import com.icuxika.seata.order.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
