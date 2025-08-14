package com.xly.marry.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PhoneLoginRequest {
    private String phoneNumber;

    private String verificationCode;
}


