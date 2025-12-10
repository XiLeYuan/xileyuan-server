package com.xly.marry.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "app.aliyun.sms")
public class SmsConfig {
    private String accessKeyId;
    private String accessKeySecret;
    private String signName;
    private String templateCode;
}
