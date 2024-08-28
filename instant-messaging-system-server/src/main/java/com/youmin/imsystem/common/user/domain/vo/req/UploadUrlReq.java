package com.youmin.imsystem.common.user.domain.vo.req;

import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UploadUrlReq {

    @NotBlank
    @ApiModelProperty("file name")
    private String fileName;

    @NotNull
    @ApiModelProperty("1. chatRoom, 2.emoji")
    private Integer sceneId;
}
