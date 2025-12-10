package com.xly.marry.controller;

import com.xly.marry.dto.ApiResponse;
import com.xly.marry.entity.User;
import com.xly.marry.service.SimpleUserService;
import com.xly.marry.util.TokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private SimpleUserService userService;

    @Autowired
    private com.xly.marry.service.OssService ossService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<User>>> getAllUsers() {
        // 这里应该添加权限验证，只有管理员才能查看所有用户
        return ResponseEntity.ok(ApiResponse.success("获取用户列表成功", null));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<User>> getUserById(@PathVariable Long id) {
        Optional<User> user = userService.findById(id);
        if (user.isPresent()) {
            return ResponseEntity.ok(ApiResponse.success("获取用户信息成功", user.get()));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<User>> getCurrentUserProfile(HttpServletRequest request) {
        User currentUser = TokenUtil.getUserFromRequest(request);
        if (currentUser == null) {
            return ResponseEntity.status(401).body(ApiResponse.error("未登录或 token 已过期"));
        }
        return ResponseEntity.ok(ApiResponse.success("获取个人信息成功", currentUser));
    }

    @PutMapping("/profile")
    public ResponseEntity<ApiResponse<User>> updateUserProfile(@RequestBody User user, HttpServletRequest request) {
        User currentUser = TokenUtil.getUserFromRequest(request);
        if (currentUser == null) {
            return ResponseEntity.status(401).body(ApiResponse.error("未登录或 token 已过期"));
        }
        // 只能更新自己的信息
        if (!currentUser.getId().equals(user.getId())) {
            return ResponseEntity.status(403).body(ApiResponse.error("无权修改其他用户信息"));
        }
        // TODO: 实现更新逻辑
        return ResponseEntity.ok(ApiResponse.success("更新个人信息成功", user));
    }

    @PostMapping("/avatar")
    public ResponseEntity<ApiResponse<String>> uploadAvatar(
            @RequestParam("file") org.springframework.web.multipart.MultipartFile file) {
        try {
            String avatarUrl = ossService.uploadFile(file);
            return ResponseEntity.ok(ApiResponse.success("头像上传成功", avatarUrl));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(ApiResponse.error("头像上传失败: " + e.getMessage()));
        }
    }

    @PostMapping("/registration/step1")
    public ResponseEntity<ApiResponse<User>> registrationStep1(
            @RequestBody com.xly.marry.dto.UserRegistrationStep1Dto dto, HttpServletRequest request) {
        User currentUser = TokenUtil.getUserFromRequest(request);
        if (currentUser == null) {
            return ResponseEntity.status(401).body(ApiResponse.error("未登录或 token 已过期"));
        }

        try {
            User updatedUser = userService.updateRegistrationStep1(currentUser.getId(), dto);
            return ResponseEntity.ok(ApiResponse.success("第一步资料注册成功", updatedUser));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(ApiResponse.error("注册失败: " + e.getMessage()));
        }
    }
}