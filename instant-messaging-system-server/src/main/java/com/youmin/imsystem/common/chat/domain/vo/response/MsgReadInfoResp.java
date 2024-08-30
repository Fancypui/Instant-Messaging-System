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
public class MsgReadInfoResp {
    @ApiModelProperty("msg id")
    private Long msgId;

    @ApiModelProperty("read count")
    private Integer readCount;

    @ApiModelProperty("unread count")
    private Integer unReadCount;
}
