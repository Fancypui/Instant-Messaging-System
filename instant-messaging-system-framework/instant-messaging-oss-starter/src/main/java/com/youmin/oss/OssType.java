package com.youmin.oss;

import io.swagger.models.auth.In;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum OssType {

    MINIO("MINIO",1);

    
    private final String name;
    private final int type;


}
