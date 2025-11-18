package com.xly.marry.controller;

import com.xly.marry.dto.ApiResponse;
import com.xly.marry.dto.PaymentRequest;
import com.xly.marry.dto.PaymentResponse;
import com.xly.marry.entity.Order;
import com.xly.marry.service.PaymentService;
import com.xly.marry.util.TokenUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;

/**
 *
 * 支付控制器
 */
@RestController
@RequestMapping("/api/payment")
@CrossOrigin(origins = "*")
public class PaymentController {
    
    @Autowired
    private PaymentService paymentService;
    
    /**
     * 创建支付订单
     */
    @PostMapping("/create")
    public ResponseEntity<ApiResponse<PaymentResponse>> createPayment(
            @RequestBody PaymentRequest request,
            HttpServletRequest httpRequest) {
        try {
            Long userId = TokenUtil.getUserIdFromRequest(httpRequest);
            if (userId == null) {
                return ResponseEntity.status(401).body(ApiResponse.error("请先登录"));
            }
            
            PaymentResponse response = paymentService.createPayment(request, userId);
            return ResponseEntity.ok(ApiResponse.success("创建支付订单成功", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * 查询订单状态
     */
    @GetMapping("/order/{orderNo}")
    public ResponseEntity<ApiResponse<Order>> queryOrder(
            @PathVariable String orderNo,
            HttpServletRequest httpRequest) {
        try {
            Long userId = TokenUtil.getUserIdFromRequest(httpRequest);
            if (userId == null) {
                return ResponseEntity.status(401).body(ApiResponse.error("请先登录"));
            }
            
            Order order = paymentService.queryOrder(orderNo);
            
            // 验证订单是否属于当前用户
            if (!order.getUserId().equals(userId)) {
                return ResponseEntity.status(403).body(ApiResponse.error("无权访问该订单"));
            }
            
            return ResponseEntity.ok(ApiResponse.success("查询成功", order));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * 取消订单
     */
    @PostMapping("/order/{orderNo}/cancel")
    public ResponseEntity<ApiResponse<String>> cancelOrder(
            @PathVariable String orderNo,
            HttpServletRequest httpRequest) {
        try {
            Long userId = TokenUtil.getUserIdFromRequest(httpRequest);
            if (userId == null) {
                return ResponseEntity.status(401).body(ApiResponse.error("请先登录"));
            }
            
            Order order = paymentService.queryOrder(orderNo);
            
            // 验证订单是否属于当前用户
            if (!order.getUserId().equals(userId)) {
                return ResponseEntity.status(403).body(ApiResponse.error("无权操作该订单"));
            }
            
            paymentService.cancelOrder(orderNo);
            return ResponseEntity.ok(ApiResponse.success("取消订单成功", "订单已取消"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * 支付宝支付回调
     */
    @PostMapping("/alipay/notify")
    public void alipayNotify(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            // 获取所有参数
            Map<String, String[]> parameterMap = request.getParameterMap();
            StringBuilder notifyData = new StringBuilder();
            for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
                notifyData.append(entry.getKey()).append("=").append(entry.getValue()[0]).append("&");
            }
            
            // 处理回调
            paymentService.handleAlipayNotify(notifyData.toString());
            
            // 返回success给支付宝
            response.getWriter().write("success");
        } catch (Exception e) {
            response.getWriter().write("fail");
        }
    }
    
    /**
     * 微信支付回调
     */
    @PostMapping("/wechat/notify")
    public void wechatNotify(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            // 读取请求体
            StringBuilder notifyData = new StringBuilder();
            String line;
            while ((line = request.getReader().readLine()) != null) {
                notifyData.append(line);
            }
            
            // 处理回调
            paymentService.handleWechatNotify(notifyData.toString());
            
            // 返回成功给微信
            response.setContentType("application/xml;charset=UTF-8");
            response.getWriter().write("<xml><return_code><![CDATA[SUCCESS]]></return_code><return_msg><![CDATA[OK]]></return_msg></xml>");
        } catch (Exception e) {
            response.setContentType("application/xml;charset=UTF-8");
            response.getWriter().write("<xml><return_code><![CDATA[FAIL]]></return_code><return_msg><![CDATA[" + e.getMessage() + "]]></return_msg></xml>");
        }
    }
}

