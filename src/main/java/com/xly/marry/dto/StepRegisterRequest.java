package com.xly.marry.dto;

import lombok.Data;

@Data
public class StepRegisterRequest {
    
    private Integer step; // 当前步骤 0-16
    
    // 步骤0: 手机验证码登录
    private String phoneNumber;

    // 步骤1: 选择性别
    private String gender;
    
    // 步骤2: 身高和年龄
    private Integer age;
    private Integer height;
    private Integer weight;
    private Boolean isWeightPrivate;
    
    // 步骤3: 学历
    private String educationLevel;
    
    // 步骤4: 学校
    private String school;
    
    // 步骤5: 公司性质和工作
    private String companyType;
    private String occupation;
    private String incomeLevel;
    
    // 步骤6: 地理位置
    private String currentProvince;
    private String currentCity;
    private String currentDistrict;
    private String hometownProvince;
    private String hometownCity;
    private String hometownDistrict;
    
    // 步骤7: 买房买车情况
    private String houseStatus;
    private String carStatus;
    
    // 步骤8: 婚姻状况
    private String maritalStatus;
    private String childrenStatus;
    
    // 步骤9: 恋爱观念
    private String loveAttitude;
    private Integer preferredAgeMin;
    private Integer preferredAgeMax;
    private String marriagePlan;
    
    // 步骤10: 昵称和头像
    private String nickname;
    private String avatarUrl;
    
    // 步骤11: 生活照
    private String lifePhotos;
    
    // 步骤12: 关于我-性格
    private String selfIntroduction;
    
    // 步骤13: 关于我-生活
    private String lifestyle;
    
    // 步骤14: 关于我-理想伴侣
    private String idealPartner;
    
    // 步骤15: 我想认识的人
    private String userTags;
    
    // 步骤16: 实名认证
    private String realName;
    private String idCardNumber;
    private String preferredTags;
} 