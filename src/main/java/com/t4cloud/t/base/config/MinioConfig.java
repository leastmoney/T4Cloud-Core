package com.t4cloud.t.base.config;

import io.minio.MinioClient;
import io.minio.errors.InvalidEndpointException;
import io.minio.errors.InvalidPortException;
import lombok.Data;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MinioConfig
 * <p>
 * 注册 minio client的bean
 * <p>
 * ---------------------
 *
 * @author TeaR
 * @date 2020/2/24 12:17
 */
@Data
@Configuration
@ConditionalOnProperty(value = "t4cloud.minio.open", havingValue = "true")
@ConfigurationProperties(prefix = "t4cloud.minio")
public class MinioConfig {

    private String endpoint;
    private String accessKey;
    private String secretKey;

    /**
     * minio client注入
     */
    @Bean
    public MinioClient initMinioClient() throws InvalidPortException, InvalidEndpointException {
        // 使用MinIO服务的URL，端口，Access key和Secret key创建一个MinioClient对象
        MinioClient minioClient = new MinioClient(endpoint, accessKey, secretKey);
        return minioClient;
    }

}
