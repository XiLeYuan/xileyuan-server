package com.xly.marry.controller;

import com.xly.marry.dto.ApiResponse;
import com.xly.marry.dto.AuthResponse;
import com.xly.marry.dto.LoginRequest;
import com.xly.marry.dto.RegisterRequest;
import com.xly.marry.dto.StepRegisterRequest;
import com.xly.marry.service.SimpleUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {
    
    @Autowired
    private SimpleUserService userService;
    
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@RequestBody RegisterRequest request) {
        try {
            AuthResponse response = userService.register(request);
            return ResponseEntity.ok(ApiResponse.success("注册成功", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @PostMapping("/step-register")
    public ResponseEntity<ApiResponse<AuthResponse>> stepRegister(@RequestBody StepRegisterRequest request) {
        try {
            AuthResponse response = userService.stepRegister(request);
            return ResponseEntity.ok(ApiResponse.success("步骤完成", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@RequestBody LoginRequest request) {
        try {
            AuthResponse response = userService.login(request);
            return ResponseEntity.ok(ApiResponse.success("登录成功", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @PostMapping("/send-verification-code")
    public ResponseEntity<ApiResponse<String>> sendVerificationCode(@RequestParam String phoneNumber) {
        try {
            // TODO: 实现发送验证码逻辑
            return ResponseEntity.ok(ApiResponse.success("验证码发送成功", "验证码已发送到" + phoneNumber));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @PostMapping("/verify-code")
    public ResponseEntity<ApiResponse<Boolean>> verifyCode(@RequestParam String phoneNumber, @RequestParam String code) {
        try {
            // TODO: 实现验证码验证逻辑
            boolean isValid = "123456".equals(code); // 临时验证逻辑
            return ResponseEntity.ok(ApiResponse.success("验证成功", isValid));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @GetMapping("/check-username")
    public ResponseEntity<ApiResponse<Boolean>> checkUsername(@RequestParam String username) {
        boolean exists = userService.findByUsername(username).isPresent();
        return ResponseEntity.ok(ApiResponse.success(exists));
    }
    
    @GetMapping("/check-email")
    public ResponseEntity<ApiResponse<Boolean>> checkEmail(@RequestParam String email) {
        boolean exists = userService.findByEmail(email).isPresent();
        return ResponseEntity.ok(ApiResponse.success(exists));
    }
    
    @GetMapping("/check-phone")
    public ResponseEntity<ApiResponse<Boolean>> checkPhone(@RequestParam String phoneNumber) {
        boolean exists = userService.findByPhoneNumber(phoneNumber).isPresent();
        return ResponseEntity.ok(ApiResponse.success(exists));
    }
    
    @GetMapping("/registration-progress")
    public ResponseEntity<ApiResponse<Integer>> getRegistrationProgress(@RequestParam String phoneNumber) {
        try {
            return userService.findByPhoneNumber(phoneNumber)
                    .map(user -> ResponseEntity.ok(ApiResponse.success(user.getRegistrationStep())))
                    .orElse(ResponseEntity.ok(ApiResponse.success(0)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
} 