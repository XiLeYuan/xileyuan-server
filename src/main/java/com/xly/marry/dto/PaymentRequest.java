package com.xly.marry.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 支付请求DTO
 */
@Data
public class PaymentRequest {
    
    @NotNull(message = "订单金额不能为空")
    @DecimalMin(value = "0.01", message = "订单金额必须大于0")
    @Digits(integer = 8, fraction = 2, message = "订单金额格式不正确")
    private BigDecimal amount;
    
    @NotBlank(message = "订单标题不能为空")
    @Size(max = 256, message = "订单标题长度不能超过256个字符")
    private String subject;
    
    private String description;
    
    @NotBlank(message = "支付方式不能为空")
    @Pattern(regexp = "ALIPAY|WECHAT", message = "支付方式必须是 ALIPAY 或 WECHAT")
    private String paymentMethod; // ALIPAY 或 WECHAT
    
    /**
     * 客户端类型：APP, H5, PC
     */
    private String clientType = "APP";
}

