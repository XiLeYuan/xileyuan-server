package com.xly.marry.repository;

import com.xly.marry.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    
    /**
     * 根据订单号查找订单
     */
    Optional<Order> findByOrderNo(String orderNo);
    
    /**
     * 根据第三方交易号查找订单
     */
    Optional<Order> findByTradeNo(String tradeNo);
    
    /**
     * 根据用户ID查找订单
     */
    java.util.List<Order> findByUserIdOrderByCreatedAtDesc(Long userId);
}

