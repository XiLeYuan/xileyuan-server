package com.xly.marry.service;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.PutObjectRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.context.SmartLifecycle;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@Service
public class OssService implements SmartLifecycle {

    private static final Logger logger = LoggerFactory.getLogger(OssService.class);

    @Value("${app.aliyun.oss.endpoint:http://oss-cn-hangzhou.aliyuncs.com}")
    private String endpoint;

    @Value("${app.aliyun.oss.access-key-id}")
    private String accessKeyId;

    @Value("${app.aliyun.oss.access-key-secret}")
    private String accessKeySecret;

    @Value("${app.aliyun.oss.bucket-name:xly-jiehunba-bucket}")
    private String bucketName;

    @Override
    public void start() {
        logger.info("OSS Config Values:");
        logger.info("Endpoint: {}", endpoint);
        logger.info("Access Key ID: {}", accessKeyId);
        logger.info("Access Key Secret: {}", accessKeySecret != null ? "******" : null); // 隐藏密钥信息
        logger.info("Bucket Name: {}", bucketName);
        
        // 打印环境变量信息用于调试
        String ossKeyIdEnv = System.getenv("ALIBABA_OSS_ACCESS_KEY_ID");
        String ossKeySecretEnv = System.getenv("ALIBABA_OSS_ACCESS_KEY_SECRET");
        logger.info("Environment Variable ALIBABA_OSS_ACCESS_KEY_ID: {}", ossKeyIdEnv != null ? "******" : null);
        logger.info("Environment Variable ALIBABA_OSS_ACCESS_KEY_SECRET: {}", ossKeySecretEnv != null ? "******" : null);
        
        // 打印所有相关环境变量
        Map<String, String> env = System.getenv();
        env.keySet().stream()
           .filter(key -> key.contains("ALIBABA") || key.contains("OSS"))
           .forEach(key -> logger.info("Related Env Var: {}={}", key, "******"));
    }

    @Override
    public void stop() {
        // 不需要特殊处理
    }

    @Override
    public boolean isRunning() {
        return false;
    }

    public String uploadFile(MultipartFile file) throws IOException {
        // 创建OSSClient实例
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

        try {
            // 获取文件后缀
            String originalFilename = file.getOriginalFilename();
            String suffix = originalFilename != null ? originalFilename.substring(originalFilename.lastIndexOf("."))
                    : "";

            // 生成唯一文件名
            String fileName = "avatars/" + UUID.randomUUID().toString() + suffix;

            // 创建PutObjectRequest对象
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, fileName, file.getInputStream());

            // 上传文件
            ossClient.putObject(putObjectRequest);

            // 返回文件URL
            // 注意：这里假设Bucket权限为公共读，或者配置了CDN。如果是私有Bucket，需要生成签名URL。
            // 格式通常为：https://bucket-name.endpoint/object-name
            return "http://" + bucketName + "." + endpoint + "/" + fileName;
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
    }
}