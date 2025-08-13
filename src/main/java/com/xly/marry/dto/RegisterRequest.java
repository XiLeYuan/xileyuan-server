package com.xly.marry.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class RegisterRequest {
    
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
    
    @Size(max = 50, message = "昵称长度不能超过50个字符")
    private String nickname;
    
    private String avatarUrl;
    
    private String gender;
    
    private Integer age;
    
    private Integer height;
    
    private Integer weight;
    
    private String educationLevel;
    
    private String occupation;
    
    private String incomeLevel;
    
    private String city;
    
    private String province;
    
    private String maritalStatus;
    
    private Boolean hasChildren;
    
    @Size(max = 1000, message = "自我介绍不能超过1000个字符")
    private String selfIntroduction;
    
    private String hobbies;
} 