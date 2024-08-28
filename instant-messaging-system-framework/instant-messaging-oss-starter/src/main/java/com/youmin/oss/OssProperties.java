package com.youmin.oss;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "oss")
public class OssProperties {

    private String secretKey;

    private String accessKey;

    private boolean enabled;

    private String endpoint;

    private String ossType;


    private String bucketName;
}
