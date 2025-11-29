package com.xly.marry.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 支付配置类
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "app.payment")
public class PaymentConfig {
    
    /**
     * 支付宝配置
     */
    private AlipayConfig alipay = new AlipayConfig();
    
    /**
     * 微信支付配置
     */
    private WechatConfig wechat = new WechatConfig();
    
    /**
     * 支付回调地址
     */
    private String notifyUrl;
    
    /**
     * 支付成功跳转地址
     */
    private String returnUrl;
    
    @Data
    public static class AlipayConfig {
        /**
         * 应用ID
         */
        private String appId;
        
        /**
         * 应用私钥
         */
        private String privateKey;
        
        /**
         * 支付宝公钥
         */
        private String publicKey;
        
        /**
         * 签名方式：RSA2
         */
        private String signType = "RSA2";
        
        /**
         * 字符编码格式
         */
        private String charset = "UTF-8";
        
        /**
         * 支付宝网关
         */
        private String gatewayUrl = "https://openapi.alipay.com/gateway.do";
        
        /**
         * 格式：JSON
         */
        private String format = "JSON";
    }
    
    @Data
    public static class WechatConfig {
        /**
         * 商户号
         */
        private String mchId;
        
        /**
         * 应用ID（AppID）
         */
        private String appId;
        
        /**
         * API密钥
         */
        private String apiKey;
        
        /**
         * 商户证书路径（用于退款等操作）
         */
        private String certPath;
        
        /**
         * 微信支付网关
         */
        private String gatewayUrl = "https://api.mch.weixin.qq.com";
    }
}

