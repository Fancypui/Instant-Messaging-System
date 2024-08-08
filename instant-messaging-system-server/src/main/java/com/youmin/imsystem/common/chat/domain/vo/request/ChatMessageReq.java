package com.youmin.imsystem.common.chat.domain.vo.request;


import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ChatMessageReq {

    @ApiModelProperty("Room id")
    @NotNull
    private Long roomId;


    /**
     * @see com.youmin.imsystem.common.chat.domain.enums.MessageTypeEnum
     */
    @ApiModelProperty("message type")
    @NotNull
    private Integer msgType;

    @ApiModelProperty("message content: different message type has different content")
    @NotNull
    private Object body;
}
