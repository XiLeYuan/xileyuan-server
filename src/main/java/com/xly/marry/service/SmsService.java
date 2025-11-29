package com.xly.marry.service;

import com.aliyun.dysmsapi20170525.Client;
import com.aliyun.dysmsapi20170525.models.SendSmsRequest;
import com.aliyun.dysmsapi20170525.models.SendSmsResponse;
import com.aliyun.teaopenapi.models.Config;
import com.xly.marry.config.SmsConfig;
import com.xly.marry.entity.VerificationCode;
import com.xly.marry.repository.VerificationCodeRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Service
@Slf4j
public class SmsService {

    @Autowired
    private SmsConfig smsConfig;

    @Autowired
    private VerificationCodeRepository verificationCodeRepository;

    private Client createClient() throws Exception {
        Config config = new Config()
                .setAccessKeyId(smsConfig.getAccessKeyId())
                .setAccessKeySecret(smsConfig.getAccessKeySecret());
        config.endpoint = "dysmsapi.aliyuncs.com";
        return new Client(config);
    }

    /**
     * 检查是否触发频率限制
     * @param clientIP 客户端IP地址
     * @param phoneNumber 手机号码
     * @return 是否受限
     */
    public boolean isRateLimited(String clientIP, String phoneNumber) {
        LocalDateTime oneMinuteAgo = LocalDateTime.now().minusMinutes(1);
        
        // 检查手机号限制（1分钟内最多1次）
        List<VerificationCode> recentCodes = verificationCodeRepository
                .findByTargetAndTypeAndCreatedAtAfter(phoneNumber, VerificationCode.Type.PHONE, oneMinuteAgo);
        
        return !recentCodes.isEmpty();
    }

    /**
     * 检查验证码是否有效（未使用且未过期）
     * @param phoneNumber 手机号
     * @return 是否有效
     */
    public boolean isVerificationCodeValid(String phoneNumber) {
        return verificationCodeRepository.findFirstByTargetAndTypeAndIsUsedFalseAndExpiresAtAfterOrderByCreatedAtDesc(
                phoneNumber, VerificationCode.Type.PHONE, LocalDateTime.now()).isPresent();
    }

    public String sendVerificationCode(String phoneNumber) throws Exception {
        return sendVerificationCode(phoneNumber, null);
    }

    public String sendVerificationCode(String phoneNumber, String clientIP) throws Exception {
        String code = String.valueOf(new Random().nextInt(9000) + 1000); // Generate 4-digit code

        Client client = createClient();
        SendSmsRequest sendSmsRequest = new SendSmsRequest()
                .setPhoneNumbers(phoneNumber)
                .setSignName(smsConfig.getSignName())
                .setTemplateCode(smsConfig.getTemplateCode())
                .setTemplateParam("{\"code\":\"" + code + "\"}");

        try {
            SendSmsResponse response = client.sendSms(sendSmsRequest);
            if (!"OK".equals(response.getBody().getCode())) {
                log.error("Failed to send SMS: " + response.getBody().getMessage());
                throw new RuntimeException("短信发送失败: " + response.getBody().getMessage());
            }
        } catch (Exception e) {
            log.error("Error sending SMS", e);
            throw new RuntimeException("短信发送服务异常");
        }

        // Save to database
        VerificationCode verificationCode = new VerificationCode();
        verificationCode.setCode(code);
        verificationCode.setTarget(phoneNumber);
        verificationCode.setType(VerificationCode.Type.PHONE);
        verificationCode.setExpiresAt(LocalDateTime.now().plusMinutes(5)); // Valid for 5 minutes
        verificationCode.setClientIP(clientIP);
        verificationCodeRepository.save(verificationCode);

        return code;
    }

    public boolean verifyCode(String phoneNumber, String code) {
        return verificationCodeRepository.findFirstByTargetAndTypeAndIsUsedFalseAndExpiresAtAfterOrderByCreatedAtDesc(
                phoneNumber, VerificationCode.Type.PHONE, LocalDateTime.now())
                .map(vc -> {
                    if (vc.getCode().equals(code)) {
                        vc.setIsUsed(true);
                        verificationCodeRepository.save(vc);
                        return true;
                    }
                    return false;
                })
                .orElse(false);
    }
}