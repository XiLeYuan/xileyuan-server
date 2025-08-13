package com.xly.marry.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User implements UserDetails {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // 基础认证信息
    @Column(unique = true, nullable = false, length = 50)
    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 50, message = "用户名长度必须在3-50个字符之间")
    private String username;
    
    @Column(unique = true, nullable = false, length = 100)
    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    private String email;
    
    @Column(nullable = false)
    @NotBlank(message = "密码不能为空")
    @Size(min = 6, message = "密码长度不能少于6个字符")
    private String password;
    
    @Column(name = "phone_number", length = 20)
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phoneNumber;
    
    // 实名认证信息
    @Column(name = "real_name", length = 50)
    @Size(max = 50, message = "真实姓名长度不能超过50个字符")
    private String realName;
    
    @Column(name = "id_card_number", length = 18)
    @Pattern(regexp = "^[1-9]\\d{5}(18|19|20)\\d{2}((0[1-9])|(1[0-2]))(([0-2][1-9])|10|20|30|31)\\d{3}[0-9Xx]$", message = "身份证号格式不正确")
    private String idCardNumber;
    
    @Column(name = "is_real_name_verified")
    private Boolean isRealNameVerified = false;
    
    // 基本信息
    @Column(name = "nickname", length = 50)
    @Size(max = 50, message = "昵称长度不能超过50个字符")
    private String nickname;
    
    @Column(name = "avatar_url")
    private String avatarUrl;
    
    @Column(name = "gender")
    @Enumerated(EnumType.STRING)
    private Gender gender;
    
    @Column(name = "birth_date")
    private LocalDateTime birthDate;
    
    @Column(name = "age")
    @Min(value = 18, message = "年龄必须大于等于18岁")
    @Max(value = 100, message = "年龄不能超过100岁")
    private Integer age;
    
    @Column(name = "height")
    @Min(value = 140, message = "身高不能少于140cm")
    @Max(value = 210, message = "身高不能超过210cm")
    private Integer height;
    
    @Column(name = "weight")
    @Min(value = 30, message = "体重不能少于30kg")
    @Max(value = 200, message = "体重不能超过200kg")
    private Integer weight;
    
    @Column(name = "is_weight_private")
    private Boolean isWeightPrivate = false;
    
    // 教育信息
    @Column(name = "education_level", length = 50)
    @Enumerated(EnumType.STRING)
    private EducationLevel educationLevel;
    
    @Column(name = "school", length = 200)
    private String school;
    
    // 职业信息
    @Column(name = "company_type")
    @Enumerated(EnumType.STRING)
    private CompanyType companyType;
    
    @Column(name = "occupation", length = 100)
    private String occupation;
    
    @Column(name = "income_level")
    @Enumerated(EnumType.STRING)
    private IncomeLevel incomeLevel;
    
    // 地理位置
    @Column(name = "current_province", length = 100)
    private String currentProvince;
    
    @Column(name = "current_city", length = 100)
    private String currentCity;
    
    @Column(name = "current_district", length = 100)
    private String currentDistrict;
    
    @Column(name = "hometown_province", length = 100)
    private String hometownProvince;
    
    @Column(name = "hometown_city", length = 100)
    private String hometownCity;
    
    @Column(name = "hometown_district", length = 100)
    private String hometownDistrict;
    
    // 资产状况
    @Column(name = "house_status")
    @Enumerated(EnumType.STRING)
    private HouseStatus houseStatus;
    
    @Column(name = "car_status")
    @Enumerated(EnumType.STRING)
    private CarStatus carStatus;
    
    // 婚姻状况
    @Column(name = "marital_status")
    @Enumerated(EnumType.STRING)
    private MaritalStatus maritalStatus;
    
    @Column(name = "children_status")
    @Enumerated(EnumType.STRING)
    private ChildrenStatus childrenStatus;
    
    // 恋爱观念
    @Column(name = "love_attitude")
    @Enumerated(EnumType.STRING)
    private LoveAttitude loveAttitude;
    
    @Column(name = "preferred_age_min")
    private Integer preferredAgeMin;
    
    @Column(name = "preferred_age_max")
    private Integer preferredAgeMax;
    
    @Column(name = "marriage_plan")
    @Enumerated(EnumType.STRING)
    private MarriagePlan marriagePlan;
    
    // 个人介绍
    @Column(name = "self_introduction", columnDefinition = "TEXT")
    @Size(max = 1000, message = "自我介绍不能超过1000个字符")
    private String selfIntroduction;
    
    @Column(name = "lifestyle", columnDefinition = "TEXT")
    @Size(max = 100, message = "生活描述不能超过100个字符")
    private String lifestyle;
    
    @Column(name = "ideal_partner", columnDefinition = "TEXT")
    @Size(max = 200, message = "理想伴侣描述不能超过200个字符")
    private String idealPartner;
    
    @Column(name = "hobbies", columnDefinition = "TEXT")
    private String hobbies;
    
    // 照片信息
    @Column(name = "life_photos", columnDefinition = "TEXT")
    private String lifePhotos; // JSON格式存储多张照片URL
    
    @Column(name = "photo_count")
    private Integer photoCount = 0;
    
    // 标签信息
    @Column(name = "user_tags", columnDefinition = "TEXT")
    private String userTags; // JSON格式存储用户选择的标签
    
    @Column(name = "preferred_tags", columnDefinition = "TEXT")
    private String preferredTags; // JSON格式存储期望伴侣的标签
    
    // 系统状态
    @Column(name = "is_verified")
    private Boolean isVerified = false;
    
    @Column(name = "is_active")
    private Boolean isActive = true;
    
    @Column(name = "registration_step")
    private Integer registrationStep = 0; // 注册步骤，0-16
    
    @Column(name = "last_login_time")
    private LocalDateTime lastLoginTime;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    private UserRole role = UserRole.USER;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    // UserDetails 接口实现
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }
    
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }
    
    @Override
    public boolean isAccountNonLocked() {
        return isActive;
    }
    
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
    
    @Override
    public boolean isEnabled() {
        return isActive;
    }
    
    // 枚举类
    public enum Gender {
        MALE, FEMALE
    }
    
    public enum EducationLevel {
        DOCTOR("博士"),
        MASTER("硕士"),
        BACHELOR("本科"),
        COLLEGE("大专"),
        BELOW_COLLEGE("大专以下");
        
        private final String displayName;
        
        EducationLevel(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
    
    public enum CompanyType {
        STATE_OWNED("国企"),
        INSTITUTION("事业单位"),
        PRIVATE("民营"),
        CIVIL_SERVANT("公务员"),
        LISTED("上市公司"),
        INDIVIDUAL("个体工商户"),
        FOREIGN("外企"),
        MILITARY("军人"),
        OTHER("其它");
        
        private final String displayName;
        
        CompanyType(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
    
    @Getter
    public enum IncomeLevel {
        BELOW_50K("5万以下"),
        FIVE_TO_TEN("5-10万"),
        TEN_TO_TWENTY("10-20万"),
        TWENTY_TO_THIRTY("20-30万"),
        THIRTY_TO_SIXTY("30-60万"),
        SIXTY_TO_HUNDRED("60-100万"),
        ABOVE_HUNDRED("100万以上");
        
        private final String displayName;
        
        IncomeLevel(String displayName) {
            this.displayName = displayName;
        }

    }
    
    public enum HouseStatus {
        NO_HOUSE("未购房"),
        HOUSE_NO_LOAN("已购房无贷款"),
        HOUSE_WITH_LOAN("已购房有贷款"),
        MULTIPLE_HOUSES("有多套房");
        
        private final String displayName;
        
        HouseStatus(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
    
    public enum CarStatus {
        NO_CAR("未购车"),
        CAR_NO_LOAN("已购车无贷款"),
        CAR_WITH_LOAN("已购车有贷款");
        
        @Getter
        private final String displayName;
        
        CarStatus(String displayName) {
            this.displayName = displayName;
        }

    }
    
    public enum MaritalStatus {
        SINGLE("未婚"),
        DIVORCED("离异"),
        WIDOWED("丧偶");
        
        private final String displayName;
        
        MaritalStatus(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
    
    public enum ChildrenStatus {
        NO_CHILDREN("没有孩子"),
        CHILDREN_WITH_SELF("有孩子随本人"),
        CHILDREN_WITH_OTHER("有孩子随对方");
        
        private final String displayName;
        
        ChildrenStatus(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
    
    public enum LoveAttitude {
        WANT_MARRY_SOON("短期内想结婚"),
        SERIOUS_LOVE_MARRY("认真谈场恋爱，合适就考虑结婚"),
        SERIOUS_LOVE_FIRST("先认真谈场恋爱再说"),
        NOT_SURE("没考虑清楚");
        
        private final String displayName;
        
        LoveAttitude(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
    
    public enum MarriagePlan {
        WITHIN_ONE_YEAR("一年内"),
        WITHIN_TWO_YEARS("两年内"),
        WITHIN_THREE_YEARS("三年内"),
        WHEN_FATE_COMES("缘分到了就结婚");
        
        private final String displayName;
        
        MarriagePlan(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
    
    public enum UserRole {
        USER, ADMIN, VIP
    }
}
