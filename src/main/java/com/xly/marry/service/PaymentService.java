package com.xly.marry.service;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AlipayTradeAppPayModel;
import com.alipay.api.request.AlipayTradeAppPayRequest;
import com.alipay.api.response.AlipayTradeAppPayResponse;
import com.xly.marry.config.PaymentConfig;
import com.xly.marry.dto.PaymentRequest;
import com.xly.marry.dto.PaymentResponse;
import com.xly.marry.entity.Order;
import com.xly.marry.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 支付服务
 */
@Service
@Transactional
public class PaymentService {
    
    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private PaymentConfig paymentConfig;
    
    private AlipayClient alipayClient;
    
    /**
     * 初始化支付宝客户端
     */
    private AlipayClient getAlipayClient() {
        if (alipayClient == null) {
            PaymentConfig.AlipayConfig alipay = paymentConfig.getAlipay();
            alipayClient = new DefaultAlipayClient(
                    alipay.getGatewayUrl(),
                    alipay.getAppId(),
                    alipay.getPrivateKey(),
                    alipay.getFormat(),
                    alipay.getCharset(),
                    alipay.getPublicKey(),
                    alipay.getSignType()
            );
        }
        return alipayClient;
    }
    
    /**
     * 创建支付订单
     */
    public PaymentResponse createPayment(PaymentRequest request, Long userId) {
        // 创建订单
        Order order = new Order();
        order.setOrderNo(generateOrderNo());
        order.setUserId(userId);
        order.setAmount(request.getAmount());
        order.setSubject(request.getSubject());
        order.setDescription(request.getDescription());
        order.setPaymentMethod(Order.PaymentMethod.valueOf(request.getPaymentMethod()));
        order.setStatus(Order.OrderStatus.PENDING);
        order.setExpiresAt(LocalDateTime.now().plusHours(2));
        
        order = orderRepository.save(order);
        
        // 根据支付方式生成支付参数
        PaymentResponse response = new PaymentResponse();
        response.setOrderNo(order.getOrderNo());
        response.setPaymentMethod(request.getPaymentMethod());
        response.setAmount(order.getAmount().toString());
        
        try {
            if ("ALIPAY".equals(request.getPaymentMethod())) {
                String payParams = createAlipayPayment(order, request.getClientType());
                response.setPayParams(payParams);
            } else if ("WECHAT".equals(request.getPaymentMethod())) {
                String payParams = createWechatPayment(order, request.getClientType());
                response.setPayParams(payParams);
                // 如果是H5或PC，可能需要返回URL或二维码
                if ("H5".equals(request.getClientType())) {
                    response.setPayUrl(payParams); // 实际应该是支付URL
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("创建支付订单失败: " + e.getMessage(), e);
        }
        
        return response;
    }
    
    /**
     * 创建支付宝支付
     */
    private String createAlipayPayment(Order order, String clientType) throws AlipayApiException {
        AlipayClient client = getAlipayClient();
        AlipayTradeAppPayRequest request = new AlipayTradeAppPayRequest();
        
        AlipayTradeAppPayModel model = new AlipayTradeAppPayModel();
        model.setOutTradeNo(order.getOrderNo());
        model.setTotalAmount(order.getAmount().toString());
        model.setSubject(order.getSubject());
        model.setBody(order.getDescription());
        model.setProductCode("QUICK_MSECURITY_PAY"); // 移动支付产品码
        
        request.setBizModel(model);
        request.setNotifyUrl(paymentConfig.getNotifyUrl() + "/api/payment/alipay/notify");
        request.setReturnUrl(paymentConfig.getReturnUrl());
        
        AlipayTradeAppPayResponse response = client.sdkExecute(request);
        return response.getBody();
    }
    
    /**
     * 创建微信支付
     */
    private String createWechatPayment(Order order, String clientType) {
        // TODO: 实现微信支付
        // 这里需要调用微信支付API创建订单
        // 返回支付参数（用于调起微信支付）
        return "{\"prepayId\":\"wx" + System.currentTimeMillis() + "\"}";
    }
    
    /**
     * 处理支付宝支付回调
     */
    public void handleAlipayNotify(String notifyData) {
        try {
            // TODO: 验证签名
            // 1. 解析回调参数
            // 2. 验证签名（使用支付宝公钥）
            // 3. 检查订单状态
            
            // 示例：从回调数据中提取订单号和交易状态
            // String orderNo = extractOrderNo(notifyData);
            // String tradeStatus = extractTradeStatus(notifyData);
            
            // 更新订单状态
            // Order order = orderRepository.findByOrderNo(orderNo)
            //         .orElseThrow(() -> new RuntimeException("订单不存在"));
            // 
            // if ("TRADE_SUCCESS".equals(tradeStatus) || "TRADE_FINISHED".equals(tradeStatus)) {
            //     order.setStatus(Order.OrderStatus.PAID);
            //     order.setTradeNo(extractTradeNo(notifyData));
            //     order.setPaidAt(LocalDateTime.now());
            //     order.setNotifyData(notifyData);
            //     orderRepository.save(order);
            //     
            //     // 处理业务逻辑（如发放会员权益等）
            // }
        } catch (Exception e) {
            throw new RuntimeException("处理支付宝回调失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 处理微信支付回调
     */
    public void handleWechatNotify(String notifyData) {
        try {
            // TODO: 验证签名并处理支付结果
            // 1. 解析XML数据
            // 2. 验证签名（使用微信API密钥）
            // 3. 检查订单状态
            
            // 示例：从回调数据中提取订单号和支付状态
            // String orderNo = extractOrderNo(notifyData);
            // String returnCode = extractReturnCode(notifyData);
            // String resultCode = extractResultCode(notifyData);
            
            // 更新订单状态
            // if ("SUCCESS".equals(returnCode) && "SUCCESS".equals(resultCode)) {
            //     Order order = orderRepository.findByOrderNo(orderNo)
            //             .orElseThrow(() -> new RuntimeException("订单不存在"));
            //     
            //     order.setStatus(Order.OrderStatus.PAID);
            //     order.setTradeNo(extractTransactionId(notifyData));
            //     order.setPaidAt(LocalDateTime.now());
            //     order.setNotifyData(notifyData);
            //     orderRepository.save(order);
            //     
            //     // 处理业务逻辑（如发放会员权益等）
            // }
        } catch (Exception e) {
            throw new RuntimeException("处理微信回调失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 查询订单状态
     */
    public Order queryOrder(String orderNo) {
        return orderRepository.findByOrderNo(orderNo)
                .orElseThrow(() -> new RuntimeException("订单不存在"));
    }
    
    /**
     * 取消订单
     */
    public void cancelOrder(String orderNo) {
        Order order = orderRepository.findByOrderNo(orderNo)
                .orElseThrow(() -> new RuntimeException("订单不存在"));
        
        if (order.getStatus() != Order.OrderStatus.PENDING) {
            throw new RuntimeException("只能取消待支付订单");
        }
        
        order.setStatus(Order.OrderStatus.CANCELLED);
        orderRepository.save(order);
    }
    
    /**
     * 生成订单号
     */
    private String generateOrderNo() {
        return "ORD" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}

