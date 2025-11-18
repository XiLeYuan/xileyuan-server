package com.xly.marry.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单实体
 */
@Entity
@Table(name = "orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * 订单号（唯一）
     */
    @Column(name = "order_no", unique = true, nullable = false, length = 64)
    private String orderNo;
    
    /**
     * 用户ID
     */
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    /**
     * 订单金额
     */
    @Column(name = "amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;
    
    /**
     * 订单标题
     */
    @Column(name = "subject", length = 256)
    private String subject;
    
    /**
     * 订单描述
     */
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    /**
     * 支付方式：ALIPAY, WECHAT
     */
    @Column(name = "payment_method", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;
    
    /**
     * 订单状态
     */
    @Column(name = "status", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private OrderStatus status = OrderStatus.PENDING;
    
    /**
     * 第三方支付订单号
     */
    @Column(name = "trade_no", length = 128)
    private String tradeNo;
    
    /**
     * 支付时间
     */
    @Column(name = "paid_at")
    private LocalDateTime paidAt;
    
    /**
     * 过期时间
     */
    @Column(name = "expires_at")
    private LocalDateTime expiresAt;
    
    /**
     * 创建时间
     */
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    /**
     * 回调通知数据（JSON格式）
     */
    @Column(name = "notify_data", columnDefinition = "TEXT")
    private String notifyData;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (expiresAt == null) {
            expiresAt = LocalDateTime.now().plusHours(2); // 默认2小时过期
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    /**
     * 支付方式枚举
     */
    public enum PaymentMethod {
        ALIPAY("支付宝"),
        WECHAT("微信支付");
        
        private final String displayName;
        
        PaymentMethod(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
    
    /**
     * 订单状态枚举
     */
    public enum OrderStatus {
        PENDING("待支付"),
        PAID("已支付"),
        CANCELLED("已取消"),
        EXPIRED("已过期"),
        REFUNDING("退款中"),
        REFUNDED("已退款");
        
        private final String displayName;
        
        OrderStatus(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
}

