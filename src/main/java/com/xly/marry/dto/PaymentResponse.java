package com.xly.marry.dto;

import lombok.Data;

/**
 * 支付响应DTO
 */
@Data
public class PaymentResponse {
    
    /**
     * 订单号
     */
    private String orderNo;
    
    /**
     * 支付方式
     */
    private String paymentMethod;
    
    /**
     * 支付参数（用于调起支付）
     */
    private String payParams;
    
    /**
     * 支付URL（H5支付时使用）
     */
    private String payUrl;
    
    /**
     * 二维码（PC支付时使用）
     */
    private String qrCode;
    
    /**
     * 订单金额
     */
    private String amount;
}

