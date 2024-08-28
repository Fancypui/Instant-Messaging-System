package com.youmin.imsystem.common.chat.domain.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageMarkDTO {

    @ApiModelProperty("operator")
    private Long uid;

    @ApiModelProperty("msgId")
    private Long msgId;

    @ApiModelProperty("1 like 2 dislike")
    private Integer markType;

    @ApiModelProperty("1 confirm 2 cancel mark")
    private Integer actType;
}
