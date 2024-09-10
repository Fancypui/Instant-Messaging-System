package com.youmin.imsystem.common.chat.domain.vo.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemberResp {

    @ApiModelProperty("group room id")
    private Long roomId;

    @ApiModelProperty("group name")
    private String groupName;


    @ApiModelProperty("group avatar url")
    private String avatar;

    @ApiModelProperty("online number")
    private Long onlineNum;

    @ApiModelProperty("group member role 1. group owner, 2 group admin 3 normal member 4 remove")
    private Integer role;
}
