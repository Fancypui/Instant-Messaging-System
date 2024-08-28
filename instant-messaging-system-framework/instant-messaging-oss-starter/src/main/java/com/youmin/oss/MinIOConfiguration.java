package com.youmin.oss;

import io.minio.MinioClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(OssProperties.class)
@ConditionalOnExpression("${oss.enabled}")
@ConditionalOnProperty(value = "oss.type",havingValue = "minio")
public class MinIOConfiguration {

    @Bean
    @ConditionalOnMissingBean(MinioClient.class)
    public MinioClient minioClient(OssProperties ossProperties){
        return MinioClient.builder()
                .credentials(ossProperties.getAccessKey(),ossProperties.getSecretKey())
                .endpoint(ossProperties.getEndpoint())
                .build();
    }


    @Bean
    @ConditionalOnBean(MinioClient.class)
    @ConditionalOnMissingBean(MinIoTemplate.class)
    public MinIoTemplate minIoTemplate(MinioClient minioClient,OssProperties ossProperties){
        return new MinIoTemplate(minioClient,ossProperties);
    }
}
