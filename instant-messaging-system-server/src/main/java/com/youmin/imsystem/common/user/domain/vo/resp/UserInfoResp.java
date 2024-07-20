package com.youmin.imsystem.common.user.domain.vo.resp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "User Info")
public class UserInfoResp {

    @ApiModelProperty(value = "User Id")
    private Long id;

    @ApiModelProperty(value = "User Name")
    private String name;

    @ApiModelProperty(value = "User Profile Picture")
    private String avatar;

    @ApiModelProperty(value = "Gender 1 male, 2 female")
    private Integer sex;

    @ApiModelProperty(value = "Remaining chance to modify name")
    private Integer modifyNameChance;
}
