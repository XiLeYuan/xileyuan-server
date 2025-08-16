package com.xly.marry.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xly.marry.dto.AuthResponse;
import com.xly.marry.dto.LoginRequest;
import com.xly.marry.dto.RegisterRequest;
import com.xly.marry.dto.StepRegisterRequest;
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
    
    @Autowired
    private ObjectMapper objectMapper;
    
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
        setUserFieldsFromRequest(user, request);
        
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
    
    public AuthResponse stepRegister(StepRegisterRequest request) {
        // 根据步骤处理注册信息
        User user = null;
        
        if (request.getStep() == 0) {
            // 步骤0: 手机验证码登录，创建临时用户
            user = new User();
            user.setPhoneNumber(request.getPhoneNumber());
            user.setUsername("temp_" + request.getPhoneNumber());
            user.setEmail("temp_" + request.getPhoneNumber() + "@temp.com");
            user.setPassword("temp_password");
            user.setRegistrationStep(0);
            user = userRepository.save(user);
        } else {
            // 查找现有用户
            user = userRepository.findByPhoneNumber(request.getPhoneNumber())
                    .orElseThrow(() -> new RuntimeException("用户不存在"));
        }
        
        // 根据步骤更新用户信息
        updateUserByStep(user, request);
        
        // 保存用户
        User savedUser = userRepository.save(user);
        
        // 如果是最后一步，生成完整令牌
        if (request.getStep() == 16) {
            String token = "token_" + savedUser.getId() + "_" + System.currentTimeMillis();
            String refreshToken = "refresh_" + savedUser.getId() + "_" + System.currentTimeMillis();
            
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
            response.setExpiresIn(86400000L);
            response.setUserInfo(userInfo);
            
            return response;
        }
        
        // 返回步骤完成响应
        AuthResponse response = new AuthResponse();
        response.setToken("step_token_" + savedUser.getId());
        response.setMessage("步骤" + request.getStep() + "完成");
        return response;
    }
    
    private void updateUserByStep(User user, StepRegisterRequest request) {
        switch (request.getStep()) {
            case 1:
                if (request.getGender() != null) {
                    try {
                        if (request.getGender().equals("1")) {
                            user.setGender(User.Gender.MALE);
                        } else {
                            user.setGender(User.Gender.FEMALE);
                        }
                    } catch (IllegalArgumentException e) {
                        throw new RuntimeException("无效的性别值");
                    }
                }
                break;
            case 2:
                user.setAge(request.getAge());
                user.setHeight(request.getHeight());
                user.setWeight(request.getWeight());
                user.setIsWeightPrivate(request.getIsWeightPrivate());
                break;
            case 3:
                if (request.getEducationLevel() != null) {
                    try {
                        user.setEducationLevel(User.EducationLevel.valueOf(request.getEducationLevel().toUpperCase()));
                    } catch (IllegalArgumentException e) {
                        throw new RuntimeException("无效的学历值");
                    }
                }
                break;
            case 4:
                user.setSchool(request.getSchool());
                break;
            case 5:
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
                break;
            case 6:
                user.setCurrentProvince(request.getCurrentProvince());
                user.setCurrentCity(request.getCurrentCity());
                user.setCurrentDistrict(request.getCurrentDistrict());
                user.setHometownProvince(request.getHometownProvince());
                user.setHometownCity(request.getHometownCity());
                user.setHometownDistrict(request.getHometownDistrict());
                break;
            case 7:
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
                break;
            case 8:
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
                break;
            case 9:
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
                break;
            case 10:
                user.setNickname(request.getNickname());
                user.setAvatarUrl(request.getAvatarUrl());
                break;
            case 11:
                user.setLifePhotos(request.getLifePhotos());
                // 计算照片数量
                if (request.getLifePhotos() != null) {
                    try {
                        String[] photos = objectMapper.readValue(request.getLifePhotos(), String[].class);
                        user.setPhotoCount(photos.length);
                    } catch (JsonProcessingException e) {
                        user.setPhotoCount(0);
                    }
                }
                break;
            case 12:
                user.setSelfIntroduction(request.getSelfIntroduction());
                break;
            case 13:
                user.setLifestyle(request.getLifestyle());
                break;
            case 14:
                user.setIdealPartner(request.getIdealPartner());
                break;
            case 15:
                user.setUserTags(request.getUserTags());
                break;
            case 16:
                user.setRealName(request.getRealName());
                user.setIdCardNumber(request.getIdCardNumber());
                user.setPreferredTags(request.getPreferredTags());
                user.setIsRealNameVerified(true);
                user.setIsVerified(true);
                break;
        }
        
        user.setRegistrationStep(request.getStep());
    }
    
    private void setUserFieldsFromRequest(User user, RegisterRequest request) {
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
        
        user.setCurrentProvince(request.getCurrentProvince());
        user.setCurrentCity(request.getCurrentCity());
        user.setCurrentDistrict(request.getCurrentDistrict());
        user.setHometownProvince(request.getHometownProvince());
        user.setHometownCity(request.getHometownCity());
        user.setHometownDistrict(request.getHometownDistrict());
        
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
        
        user.setSelfIntroduction(request.getSelfIntroduction());
        user.setLifestyle(request.getLifestyle());
        user.setIdealPartner(request.getIdealPartner());
        user.setHobbies(request.getHobbies());
        user.setLifePhotos(request.getLifePhotos());
        user.setUserTags(request.getUserTags());
        user.setPreferredTags(request.getPreferredTags());
        user.setRealName(request.getRealName());
        user.setIdCardNumber(request.getIdCardNumber());
        user.setRegistrationStep(request.getRegistrationStep());
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
    
    public AuthResponse phoneLoginOrRegister(String phoneNumber) {
        // 查找用户是否存在
        Optional<User> userOpt = userRepository.findByPhoneNumber(phoneNumber);
        
        if (userOpt.isPresent()) {
            // 用户存在，执行登录
            User user = userOpt.get();
            
            // 检查用户是否被禁用
            if (!user.getIsActive()) {
                throw new RuntimeException("账户已被禁用");
            }
            
            // 生成令牌
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
            response.setMessage("登录成功");
            
            return response;
        } else {
            // 用户不存在，创建临时用户开始注册流程
            User newUser = new User();
            newUser.setPhoneNumber(phoneNumber);
            newUser.setUsername("temp_" + phoneNumber);
            newUser.setEmail("temp_" + phoneNumber + "@temp.com");
            newUser.setPassword("temp_password");
            newUser.setRegistrationStep(0);
            newUser.setIsActive(true);
            
            User savedUser = userRepository.save(newUser);
            
            // 生成临时令牌
            String token = "temp_token_" + savedUser.getId() + "_" + System.currentTimeMillis();
            
            AuthResponse response = new AuthResponse();
            response.setToken(token);
            response.setExpiresIn(86400000L); // 24小时
            response.setMessage("开始注册流程");
            
            return response;
        }
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
    
    public Optional<User> findByPhoneNumber(String phoneNumber) {
        return userRepository.findByPhoneNumber(phoneNumber);
    }
} 