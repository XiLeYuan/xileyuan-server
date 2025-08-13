package com.xly.marry.service;

import com.xly.marry.dto.AuthResponse;
import com.xly.marry.dto.LoginRequest;
import com.xly.marry.dto.RegisterRequest;
import com.xly.marry.entity.User;
import com.xly.marry.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Transactional
public class SimpleUserService {
    
    @Autowired
    private UserRepository userRepository;
    
    public AuthResponse register(RegisterRequest request) {
        // 验证密码确认
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new RuntimeException("密码和确认密码不匹配");
        }
        
        // 检查用户名是否已存在
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("用户名已存在");
        }
        
        // 检查邮箱是否已存在
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("邮箱已被注册");
        }
        
        // 检查手机号是否已存在
        if (request.getPhoneNumber() != null && userRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new RuntimeException("手机号已被注册");
        }
        
        // 创建新用户
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        // 简单的MD5加密（生产环境建议使用BCrypt）
        user.setPassword(DigestUtils.md5DigestAsHex(request.getPassword().getBytes()));
        user.setPhoneNumber(request.getPhoneNumber());
        user.setNickname(request.getNickname() != null ? request.getNickname() : request.getUsername());
        user.setAvatarUrl(request.getAvatarUrl());
        
        // 设置其他字段
        if (request.getGender() != null) {
            try {
                user.setGender(User.Gender.valueOf(request.getGender().toUpperCase()));
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("无效的性别值");
            }
        }
        
        user.setAge(request.getAge());
        user.setHeight(request.getHeight());
        user.setWeight(request.getWeight());
        user.setEducationLevel(request.getEducationLevel());
        user.setOccupation(request.getOccupation());
        user.setIncomeLevel(request.getIncomeLevel());
        user.setCity(request.getCity());
        user.setProvince(request.getProvince());
        
        if (request.getMaritalStatus() != null) {
            try {
                user.setMaritalStatus(User.MaritalStatus.valueOf(request.getMaritalStatus().toUpperCase()));
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("无效的婚姻状况值");
            }
        }
        
        user.setHasChildren(request.getHasChildren());
        user.setSelfIntroduction(request.getSelfIntroduction());
        user.setHobbies(request.getHobbies());
        
        // 保存用户
        User savedUser = userRepository.save(user);
        
        // 生成简单的令牌（实际应该使用JWT）
        String token = "token_" + savedUser.getId() + "_" + System.currentTimeMillis();
        String refreshToken = "refresh_" + savedUser.getId() + "_" + System.currentTimeMillis();
        
        // 更新最后登录时间
        savedUser.setLastLoginTime(LocalDateTime.now());
        userRepository.save(savedUser);
        
        // 构建响应
        AuthResponse.UserInfo userInfo = new AuthResponse.UserInfo();
        userInfo.setId(savedUser.getId());
        userInfo.setUsername(savedUser.getUsername());
        userInfo.setEmail(savedUser.getEmail());
        userInfo.setNickname(savedUser.getNickname());
        userInfo.setAvatarUrl(savedUser.getAvatarUrl());
        userInfo.setRole(savedUser.getRole().name());
        userInfo.setIsVerified(savedUser.getIsVerified());
        
        AuthResponse response = new AuthResponse();
        response.setToken(token);
        response.setRefreshToken(refreshToken);
        response.setExpiresIn(86400000L); // 24小时
        response.setUserInfo(userInfo);
        
        return response;
    }
    
    public AuthResponse login(LoginRequest request) {
        // 查找用户
        Optional<User> userOpt = userRepository.findByUsernameOrEmail(request.getUsernameOrEmail());
        if (userOpt.isEmpty()) {
            throw new RuntimeException("用户名或密码错误");
        }
        
        User user = userOpt.get();
        
        // 验证密码
        String encryptedPassword = DigestUtils.md5DigestAsHex(request.getPassword().getBytes());
        if (!user.getPassword().equals(encryptedPassword)) {
            throw new RuntimeException("用户名或密码错误");
        }
        
        // 检查用户是否被禁用
        if (!user.getIsActive()) {
            throw new RuntimeException("账户已被禁用");
        }
        
        // 生成简单的令牌
        String token = "token_" + user.getId() + "_" + System.currentTimeMillis();
        String refreshToken = "refresh_" + user.getId() + "_" + System.currentTimeMillis();
        
        // 更新最后登录时间
        user.setLastLoginTime(LocalDateTime.now());
        userRepository.save(user);
        
        // 构建响应
        AuthResponse.UserInfo userInfo = new AuthResponse.UserInfo();
        userInfo.setId(user.getId());
        userInfo.setUsername(user.getUsername());
        userInfo.setEmail(user.getEmail());
        userInfo.setNickname(user.getNickname());
        userInfo.setAvatarUrl(user.getAvatarUrl());
        userInfo.setRole(user.getRole().name());
        userInfo.setIsVerified(user.getIsVerified());
        
        AuthResponse response = new AuthResponse();
        response.setToken(token);
        response.setRefreshToken(refreshToken);
        response.setExpiresIn(86400000L); // 24小时
        response.setUserInfo(userInfo);
        
        return response;
    }
    
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }
} 