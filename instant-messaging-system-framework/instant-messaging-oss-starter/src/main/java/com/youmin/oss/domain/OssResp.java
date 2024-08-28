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
public class OssResp {

    @ApiModelProperty("temporary upload url")
    private String uploadUrl;

    @ApiModelProperty("download url")
    private String downloadUrl;
}
