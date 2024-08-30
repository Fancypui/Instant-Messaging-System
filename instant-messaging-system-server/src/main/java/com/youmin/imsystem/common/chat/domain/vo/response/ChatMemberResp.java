package com.youmin.imsystem.common.chat.domain.vo.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatMemberResp {

    @ApiModelProperty("user id")
    private Long uid;

    @ApiModelProperty("online status 1 online 2 offline")
    private Integer activeStatus;

    @ApiModelProperty("last online time")
    private Date lastOptTime;

    private Integer roleId;
}
