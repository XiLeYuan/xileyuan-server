package com.xly.marry.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
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
    
    @Column(name = "real_name", length = 50)
    @Size(max = 50, message = "真实姓名长度不能超过50个字符")
    private String realName;
    
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
    @Min(value = 100, message = "身高不能少于100cm")
    @Max(value = 250, message = "身高不能超过250cm")
    private Integer height;
    
    @Column(name = "weight")
    @Min(value = 30, message = "体重不能少于30kg")
    @Max(value = 200, message = "体重不能超过200kg")
    private Integer weight;
    
    @Column(name = "education_level", length = 50)
    private String educationLevel;
    
    @Column(name = "occupation", length = 100)
    private String occupation;
    
    @Column(name = "income_level", length = 50)
    private String incomeLevel;
    
    @Column(name = "city", length = 100)
    private String city;
    
    @Column(name = "province", length = 100)
    private String province;
    
    @Column(name = "marital_status")
    @Enumerated(EnumType.STRING)
    private MaritalStatus maritalStatus;
    
    @Column(name = "has_children")
    private Boolean hasChildren;
    
    @Column(name = "self_introduction", columnDefinition = "TEXT")
    @Size(max = 1000, message = "自我介绍不能超过1000个字符")
    private String selfIntroduction;
    
    @Column(name = "hobbies", columnDefinition = "TEXT")
    private String hobbies;
    
    @Column(name = "is_verified")
    private Boolean isVerified = false;
    
    @Column(name = "is_active")
    private Boolean isActive = true;
    
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
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
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
        MALE, FEMALE, OTHER
    }
    
    public enum MaritalStatus {
        SINGLE, DIVORCED, WIDOWED
    }
    
    public enum UserRole {
        USER, ADMIN, VIP
    }
}
