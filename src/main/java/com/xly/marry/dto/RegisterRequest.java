package com.xly.marry.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class RegisterRequest {
    
    // 基础认证信息
    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 50, message = "用户名长度必须在3-50个字符之间")
    private String username;
    
    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    private String email;
    
    @NotBlank(message = "密码不能为空")
    @Size(min = 6, message = "密码长度不能少于6个字符")
    private String password;
    
    @NotBlank(message = "确认密码不能为空")
    private String confirmPassword;
    
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phoneNumber;
    
    // 实名认证信息
    @Size(max = 50, message = "真实姓名长度不能超过50个字符")
    private String realName;
    
    @Pattern(regexp = "^[1-9]\\d{5}(18|19|20)\\d{2}((0[1-9])|(1[0-2]))(([0-2][1-9])|10|20|30|31)\\d{3}[0-9Xx]$", message = "身份证号格式不正确")
    private String idCardNumber;
    
    // 基本信息
    @Size(max = 50, message = "昵称长度不能超过50个字符")
    private String nickname;
    
    private String avatarUrl;
    
    private String gender;
    
    private Integer age;
    
    private Integer height;
    
    private Integer weight;
    
    private Boolean isWeightPrivate = false;
    
    // 教育信息
    private String educationLevel;
    
    private String school;
    
    // 职业信息
    private String companyType;
    
    private String occupation;
    
    private String incomeLevel;
    
    // 地理位置
    private String currentProvince;
    
    private String currentCity;
    
    private String currentDistrict;
    
    private String hometownProvince;
    
    private String hometownCity;
    
    private String hometownDistrict;
    
    // 资产状况
    private String houseStatus;
    
    private String carStatus;
    
    // 婚姻状况
    private String maritalStatus;
    
    private String childrenStatus;
    
    // 恋爱观念
    private String loveAttitude;
    
    private Integer preferredAgeMin;
    
    private Integer preferredAgeMax;
    
    private String marriagePlan;
    
    // 个人介绍
    @Size(max = 1000, message = "自我介绍不能超过1000个字符")
    private String selfIntroduction;
    
    @Size(max = 100, message = "生活描述不能超过100个字符")
    private String lifestyle;
    
    @Size(max = 200, message = "理想伴侣描述不能超过200个字符")
    private String idealPartner;
    
    private String hobbies;
    
    // 照片信息
    private String lifePhotos; // JSON格式存储多张照片URL
    
    // 标签信息
    private String userTags; // JSON格式存储用户选择的标签
    
    private String preferredTags; // JSON格式存储期望伴侣的标签
    
    // 注册步骤
    private Integer registrationStep = 0;
} 