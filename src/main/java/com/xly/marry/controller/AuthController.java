package com.xly.marry.controller;

import com.xly.marry.dto.*;
import com.xly.marry.entity.User;
import com.xly.marry.service.SimpleUserService;
import com.xly.marry.service.TokenService;
import com.xly.marry.service.SmsService;
import com.xly.marry.util.TokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Pattern;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {
    
    @Autowired
    private SimpleUserService userService;
    
    @Autowired
    private TokenService tokenService;

    @Autowired
    private SmsService smsService;
    
    /**
     * 手机验证码登录/注册（合二为一）
     * 如果用户存在则登录，不存在则创建临时用户开始注册流程
     */

    @PostMapping("/phoneLogin")
    public ResponseEntity<ApiResponse<AuthResponse>> phoneLoginWithCode(
            @RequestBody PhoneLoginRequest phoneLoginRequest) {
        try {
            if (!smsService.verifyCode(phoneLoginRequest.getPhoneNumber(), phoneLoginRequest.getVerificationCode())) {
                return ResponseEntity.badRequest().body(ApiResponse.error("验证码错误或已过期"));
            }

            AuthResponse response = userService.phoneLoginOrRegister(phoneLoginRequest.getPhoneNumber());
            return ResponseEntity.ok(ApiResponse.success("登录成功", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * 分步注册（主要注册方式）
     */
    @PostMapping("/stepRegister")
    public ResponseEntity<ApiResponse<AuthResponse>> stepRegister(
            @RequestBody StepRegisterRequest request,
            HttpServletRequest httpRequest) {
        try {
            String token = TokenUtil.getTokenFromRequest(httpRequest);
            if (token == null || token.isEmpty()) {
                return ResponseEntity.status(401).body(ApiResponse.error("请先登录"));
            }
            AuthResponse response = userService.stepRegister(request, token);
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
     * 发送手机验证码
     *
     * 该接口用于向指定手机号发送短信验证码，用于用户身份验证。
     * 验证码默认有效期为5分钟，每个手机号每分钟只能发送一次。
     *
     * @param phoneNumber 用户手机号，必须是11位中国大陆手机号
     * @param request HTTP请求对象，用于获取客户端IP地址
     * @return ApiResponse<String> 统一响应格式
     *         - 成功: {code: 200, message: "验证码发送成功", data: "验证码已发送到xxx"}
     *         - 失败: {code: 400, message: "错误原因", data: null}
     *
     * @throws Exception 当系统内部发生错误时抛出
     */
    @PostMapping("/send-verification-code")
    public ResponseEntity<ApiResponse<String>> sendVerificationCode(
            @RequestParam @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确") String phoneNumber,
            HttpServletRequest request) {
        try {
            // 1. 防刷校验：同一IP或手机号发送频率限制
            String clientIP = getClientIP(request);
            if (smsService.isRateLimited(clientIP, phoneNumber)) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error("发送过于频繁，请稍后再试"));
            }

            // 2. 检查该手机号是否已经发送过验证码且未过期
            if (smsService.isVerificationCodeValid(phoneNumber)) {
                return ResponseEntity.ok(ApiResponse.success("验证码已发送", "验证码已发送到" + phoneNumber));
            }

            // 3. 调用服务发送验证码
            smsService.sendVerificationCode(phoneNumber);
            
            return ResponseEntity.ok(ApiResponse.success("验证码发送成功", "验证码已发送到" + phoneNumber));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * 获取客户端真实IP地址
     * @param request HTTP请求
     * @return 客户端IP地址
     */
    private String getClientIP(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0];
    }
    
    /**
     * 验证验证码
     */
    @PostMapping("/verify-code")
    public ResponseEntity<ApiResponse<Boolean>> verifyCode(@RequestParam String phoneNumber, @RequestParam String code) {
        try {
            boolean isValid = smsService.verifyCode(phoneNumber, code);
            if (isValid) {
                return ResponseEntity.ok(ApiResponse.success("验证成功", null));
            } else {
                return ResponseEntity.ok(ApiResponse.error("验证失败"));
            }
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
    
    /**
     * 获取当前登录用户信息
     */
    @GetMapping("/current-user")
    public ResponseEntity<ApiResponse<AuthResponse.UserInfo>> getCurrentUser(HttpServletRequest request) {
        try {
            User user = TokenUtil.getUserFromRequest(request);
            if (user == null) {
                return ResponseEntity.status(401).body(ApiResponse.error("未登录或 token 已过期"));
            }
            
            AuthResponse.UserInfo userInfo = new AuthResponse.UserInfo();
            userInfo.setId(user.getId());
            userInfo.setUsername(user.getUsername());
            userInfo.setEmail(user.getEmail());
            userInfo.setNickname(user.getNickname());
            userInfo.setAvatarUrl(user.getAvatarUrl());
            userInfo.setRole(user.getRole().name());
            userInfo.setIsVerified(user.getIsVerified());
            
            return ResponseEntity.ok(ApiResponse.success("获取成功", userInfo));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * 登出
     */
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<String>> logout(HttpServletRequest request) {
        try {
            String token = TokenUtil.getTokenFromRequest(request);
            if (token != null && !token.isEmpty()) {
                tokenService.removeToken(token);
            }
            return ResponseEntity.ok(ApiResponse.success("登出成功", "已成功登出"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * 刷新 token
     */
    @PostMapping("/refresh-token")
    public ResponseEntity<ApiResponse<AuthResponse>> refreshToken(HttpServletRequest request) {
        try {
            String oldToken = TokenUtil.getTokenFromRequest(request);
            if (oldToken == null || oldToken.isEmpty()) {
                return ResponseEntity.status(401).body(ApiResponse.error("未提供 token"));
            }
            
            String newToken = tokenService.refreshToken(oldToken);
            if (newToken == null) {
                return ResponseEntity.status(401).body(ApiResponse.error("token 无效或已过期"));
            }
            
            User user = tokenService.validateToken(newToken);
            AuthResponse.UserInfo userInfo = new AuthResponse.UserInfo();
            userInfo.setId(user.getId());
            userInfo.setUsername(user.getUsername());
            userInfo.setEmail(user.getEmail());
            userInfo.setNickname(user.getNickname());
            userInfo.setAvatarUrl(user.getAvatarUrl());
            userInfo.setRole(user.getRole().name());
            userInfo.setIsVerified(user.getIsVerified());
            
            AuthResponse response = new AuthResponse();
            response.setToken(newToken);
            response.setTokenType("Bearer");
            response.setExpiresIn(86400000L);
            response.setUserInfo(userInfo);
            
            return ResponseEntity.ok(ApiResponse.success("刷新成功", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
}