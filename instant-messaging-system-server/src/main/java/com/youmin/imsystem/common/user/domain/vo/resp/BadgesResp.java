package com.youmin.imsystem.common.user.domain.vo.resp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "User Info")
public class BadgesResp {

    @ApiModelProperty(value = "item config id")
    private Long id;

    @ApiModelProperty(value = "Badge Img Url")
    private String img;

    @ApiModelProperty(value = "Badge description")
    private String describe;

    @ApiModelProperty(value = "Whether user has obtained the badge, 0 no; 1 yes")
    private Integer obtained;

    @ApiModelProperty(value = "Whether user is wearing the badge, 0 no; 1 yes")
    private Integer wearing;
}
