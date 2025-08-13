package com.xly.marry.controller;

import com.xly.marry.dto.ApiResponse;
import com.xly.marry.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/test")
@CrossOrigin(origins = "*")
public class TestController {
    
    @Autowired
    private CustomUserDetailsService userDetailsService;
    
    @GetMapping("/health")
    public ResponseEntity<ApiResponse<Map<String, Object>>> health() {
        Map<String, Object> data = new HashMap<>();
        data.put("status", "UP");
        data.put("timestamp", LocalDateTime.now());
        data.put("service", "Marry Social App Backend");
        data.put("version", "1.0.0");
        
        return ResponseEntity.ok(ApiResponse.success("服务运行正常", data));
    }
    
    @GetMapping("/hello")
    public ResponseEntity<ApiResponse<String>> hello() {
        return ResponseEntity.ok(ApiResponse.success("Hello from Marry App!", "欢迎使用相亲社交APP"));
    }
    
    @GetMapping("/test-user-details")
    public ResponseEntity<ApiResponse<Map<String, Object>>> testUserDetails(@RequestParam String username) {
        try {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            
            Map<String, Object> data = new HashMap<>();
            data.put("username", userDetails.getUsername());
            data.put("authorities", userDetails.getAuthorities());
            data.put("accountNonExpired", userDetails.isAccountNonExpired());
            data.put("accountNonLocked", userDetails.isAccountNonLocked());
            data.put("credentialsNonExpired", userDetails.isCredentialsNonExpired());
            data.put("enabled", userDetails.isEnabled());
            
            return ResponseEntity.ok(ApiResponse.success("UserDetails测试成功", data));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("UserDetails测试失败: " + e.getMessage()));
        }
    }
} 