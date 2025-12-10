package com.xly.marry.service;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.PutObjectRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
public class OssService {

    @Value("${app.aliyun.oss.endpoint}")
    private String endpoint;

    @Value("${app.aliyun.oss.access-key-id}")
    private String accessKeyId;

    @Value("${app.aliyun.oss.access-key-secret}")
    private String accessKeySecret;

    @Value("${app.aliyun.oss.bucket-name}")
    private String bucketName;

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
