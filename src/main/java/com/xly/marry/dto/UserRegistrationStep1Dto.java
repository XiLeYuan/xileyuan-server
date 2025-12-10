package com.xly.marry.dto;

import com.xly.marry.entity.User;
import lombok.Data;

@Data
public class UserRegistrationStep1Dto {
    private String avatarUrl;
    private String nickname;
    private User.Gender gender;
    private Integer age;
    private Integer height;
    private String hometownProvince;
    private String hometownCity;
    private String hometownDistrict;
    private String currentProvince;
    private String currentCity;
    private String currentDistrict;
}
