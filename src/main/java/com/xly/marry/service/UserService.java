// src/main/java/com/xly/marry/service/UserService.java
package com.xly.marry.service;

import com.xly.marry.dto.AuthResponse;
import com.xly.marry.dto.LoginRequest;
import com.xly.marry.dto.RegisterRequest;
import com.xly.marry.entity.User;
import com.xly.marry.repository.UserRepository;
import com.xly.marry.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Transactional
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    
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
        user.setPassword(passwordEncoder.encode(request.getPassword()));
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
        user.setIsWeightPrivate(request.getIsWeightPrivate());
        
        if (request.getEducationLevel() != null) {
            try {
                user.setEducationLevel(User.EducationLevel.valueOf(request.getEducationLevel().toUpperCase()));
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("无效的学历值");
            }
        }
        
        user.setSchool(request.getSchool());
        
        if (request.getCompanyType() != null) {
            try {
                user.setCompanyType(User.CompanyType.valueOf(request.getCompanyType().toUpperCase()));
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("无效的公司类型值");
            }
        }
        
        user.setOccupation(request.getOccupation());
        
        if (request.getIncomeLevel() != null) {
            try {
                user.setIncomeLevel(User.IncomeLevel.valueOf(request.getIncomeLevel().toUpperCase()));
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("无效的收入水平值");
            }
        }
        
        // 地理位置
        user.setCurrentProvince(request.getCurrentProvince());
        user.setCurrentCity(request.getCurrentCity());
        user.setCurrentDistrict(request.getCurrentDistrict());
        user.setHometownProvince(request.getHometownProvince());
        user.setHometownCity(request.getHometownCity());
        user.setHometownDistrict(request.getHometownDistrict());
        
        // 资产状况
        if (request.getHouseStatus() != null) {
            try {
                user.setHouseStatus(User.HouseStatus.valueOf(request.getHouseStatus().toUpperCase()));
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("无效的购房状态值");
            }
        }
        
        if (request.getCarStatus() != null) {
            try {
                user.setCarStatus(User.CarStatus.valueOf(request.getCarStatus().toUpperCase()));
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("无效的购车状态值");
            }
        }
        
        // 婚姻状况
        if (request.getMaritalStatus() != null) {
            try {
                user.setMaritalStatus(User.MaritalStatus.valueOf(request.getMaritalStatus().toUpperCase()));
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("无效的婚姻状况值");
            }
        }
        
        if (request.getChildrenStatus() != null) {
            try {
                user.setChildrenStatus(User.ChildrenStatus.valueOf(request.getChildrenStatus().toUpperCase()));
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("无效的子女状况值");
            }
        }
        
        // 恋爱观念
        if (request.getLoveAttitude() != null) {
            try {
                user.setLoveAttitude(User.LoveAttitude.valueOf(request.getLoveAttitude().toUpperCase()));
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("无效的恋爱态度值");
            }
        }
        
        user.setPreferredAgeMin(request.getPreferredAgeMin());
        user.setPreferredAgeMax(request.getPreferredAgeMax());
        
        if (request.getMarriagePlan() != null) {
            try {
                user.setMarriagePlan(User.MarriagePlan.valueOf(request.getMarriagePlan().toUpperCase()));
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("无效的结婚计划值");
            }
        }
        
        // 个人介绍
        user.setSelfIntroduction(request.getSelfIntroduction());
        user.setLifestyle(request.getLifestyle());
        user.setIdealPartner(request.getIdealPartner());
        user.setHobbies(request.getHobbies());
        
        // 照片和标签
        user.setLifePhotos(request.getLifePhotos());
        user.setUserTags(request.getUserTags());
        user.setPreferredTags(request.getPreferredTags());
        
        // 实名认证
        user.setRealName(request.getRealName());
        user.setIdCardNumber(request.getIdCardNumber());
        user.setRegistrationStep(request.getRegistrationStep());
        
        // 保存用户
        User savedUser = userRepository.save(user);
        
        // 生成令牌
        String token = jwtTokenProvider.generateTokenFromUsername(savedUser.getUsername());
        String refreshToken = jwtTokenProvider.generateRefreshToken(savedUser.getUsername());
        
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
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("用户名或密码错误");
        }
        
        // 检查用户是否被禁用
        if (!user.getIsActive()) {
            throw new RuntimeException("账户已被禁用");
        }
        
        // 生成令牌
        String token = jwtTokenProvider.generateTokenFromUsername(user.getUsername());
        String refreshToken = jwtTokenProvider.generateRefreshToken(user.getUsername());
        
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