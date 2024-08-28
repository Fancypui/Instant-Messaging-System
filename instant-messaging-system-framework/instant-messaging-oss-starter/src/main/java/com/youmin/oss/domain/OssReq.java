package com.youmin.oss.domain;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OssReq {

    @ApiModelProperty("filename")
    private String filename;

    @ApiModelProperty("file storage location")
    private String filePath;

    @ApiModelProperty("user id")
    private Long uid;
    @ApiModelProperty(value = "auto generate file storage location")
    @Builder.Default
    private boolean autoPath = true;

}
