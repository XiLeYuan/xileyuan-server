package com.xly.marry.controller;

import com.xly.marry.dto.ApiResponse;
import com.xly.marry.entity.User;
import com.xly.marry.service.SimpleUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {
    
    @Autowired
    private SimpleUserService userService;
    
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
    public ResponseEntity<ApiResponse<User>> getCurrentUserProfile() {
        // 这里应该从JWT token中获取当前用户信息
        return ResponseEntity.ok(ApiResponse.success("获取个人信息成功", null));
    }
    
    @PutMapping("/profile")
    public ResponseEntity<ApiResponse<User>> updateUserProfile(@RequestBody User user) {
        // 这里应该验证当前用户身份并更新信息
        return ResponseEntity.ok(ApiResponse.success("更新个人信息成功", null));
    }
}