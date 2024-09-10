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
public class ChatMemberListResp {


    @ApiModelProperty("uid")
    private Long uid;

    @ApiModelProperty("user name")
    private String name;

    @ApiModelProperty("user avatar")
    private String avatar;
}
