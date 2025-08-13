package com.xly.marry.controller;

import com.xly.marry.dto.ApiResponse;
import com.xly.marry.dto.AuthResponse;
import com.xly.marry.dto.LoginRequest;
import com.xly.marry.dto.RegisterRequest;
import com.xly.marry.dto.StepRegisterRequest;
import com.xly.marry.entity.User;
import com.xly.marry.service.SimpleUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {
    
    @Autowired
    private SimpleUserService userService;
    
    /**
     * 手机验证码登录/注册（合二为一）
     * 如果用户存在则登录，不存在则创建临时用户开始注册流程
     */
    @PostMapping("/phone-login")
    public ResponseEntity<ApiResponse<AuthResponse>> phoneLogin(@RequestParam String phoneNumber, @RequestParam String verificationCode) {
        try {
            // TODO: 验证验证码
            if (!"123456".equals(verificationCode)) {
                return ResponseEntity.badRequest().body(ApiResponse.error("验证码错误"));
            }
            
            AuthResponse response = userService.phoneLoginOrRegister(phoneNumber);
            return ResponseEntity.ok(ApiResponse.success("登录成功", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * 分步注册（主要注册方式）
     */
    @PostMapping("/step-register")
    public ResponseEntity<ApiResponse<AuthResponse>> stepRegister(@RequestBody StepRegisterRequest request) {
        try {
            AuthResponse response = userService.stepRegister(request);
            return ResponseEntity.ok(ApiResponse.success("步骤完成", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * 传统用户名密码登录（备用方案）
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@RequestBody LoginRequest request) {
        try {
            AuthResponse response = userService.login(request);
            return ResponseEntity.ok(ApiResponse.success("登录成功", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * 完整注册（备用方案，主要用于测试）
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@RequestBody RegisterRequest request) {
        try {
            AuthResponse response = userService.register(request);
            return ResponseEntity.ok(ApiResponse.success("注册成功", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * 发送验证码
     */
    @PostMapping("/send-verification-code")
    public ResponseEntity<ApiResponse<String>> sendVerificationCode(@RequestParam String phoneNumber) {
        try {
            // TODO: 实现发送验证码逻辑
            return ResponseEntity.ok(ApiResponse.success("验证码发送成功", "验证码已发送到" + phoneNumber));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * 验证验证码
     */
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
    
    /**
     * 检查用户名是否存在
     */
    @GetMapping("/check-username")
    public ResponseEntity<ApiResponse<Boolean>> checkUsername(@RequestParam String username) {
        boolean exists = userService.findByUsername(username).isPresent();
        return ResponseEntity.ok(ApiResponse.success(exists));
    }
    
    /**
     * 检查邮箱是否存在
     */
    @GetMapping("/check-email")
    public ResponseEntity<ApiResponse<Boolean>> checkEmail(@RequestParam String email) {
        boolean exists = userService.findByEmail(email).isPresent();
        return ResponseEntity.ok(ApiResponse.success(exists));
    }
    
    /**
     * 检查手机号是否存在
     */
    @GetMapping("/check-phone")
    public ResponseEntity<ApiResponse<Boolean>> checkPhone(@RequestParam String phoneNumber) {
        boolean exists = userService.findByPhoneNumber(phoneNumber).isPresent();
        return ResponseEntity.ok(ApiResponse.success(exists));
    }
    
    /**
     * 获取注册进度
     */
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
    
    /**
     * 获取用户注册状态
     */
    @GetMapping("/user-status")
    public ResponseEntity<ApiResponse<Object>> getUserStatus(@RequestParam String phoneNumber) {
        try {
            Optional<User> userOpt = userService.findByPhoneNumber(phoneNumber);
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                // 构建用户状态信息
                var status = new java.util.HashMap<String, Object>();
                status.put("exists", true);
                status.put("registrationStep", user.getRegistrationStep());
                status.put("isVerified", user.getIsVerified());
                status.put("isComplete", user.getRegistrationStep() >= 16);
                status.put("userId", user.getId());
                return ResponseEntity.ok(ApiResponse.success(status));
            } else {
                return ResponseEntity.ok(ApiResponse.success(java.util.Map.of("exists", false)));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
} 