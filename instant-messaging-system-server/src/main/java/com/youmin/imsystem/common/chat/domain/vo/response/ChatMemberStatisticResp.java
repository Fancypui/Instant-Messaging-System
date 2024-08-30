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
public class ChatMemberStatisticResp {
    @ApiModelProperty("online number")
    private Long onlineNum;//在线人数
    @ApiModelProperty("total number")
    @Deprecated
    private Long totalNum;//总人数
}
