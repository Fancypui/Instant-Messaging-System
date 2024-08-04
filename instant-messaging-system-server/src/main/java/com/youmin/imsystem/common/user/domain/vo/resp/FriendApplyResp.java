package com.youmin.imsystem.common.user.domain.vo.resp;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FriendApplyResp {

    @ApiModelProperty("Requester Uid")
    private Long uid;

    @ApiModelProperty("Requester Name")
    private String name;

    @ApiModelProperty("Apply Type, 1 Add as friend")
    private Integer type;

    @ApiModelProperty("User Apply Id")
    private Long applyId;

    @ApiModelProperty("Requester apply remark")
    private String msg;

    @ApiModelProperty("Status Code 1 Pending,2Approve")
    private Integer status;

    @ApiModelProperty("Avatar Url")
    private String avatar;
}
