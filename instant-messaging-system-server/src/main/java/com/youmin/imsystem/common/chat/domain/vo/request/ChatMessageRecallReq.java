package com.youmin.imsystem.common.chat.domain.vo.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class ChatMessageRecallReq {

    @NotNull
    @ApiModelProperty("msg id")
    private Long msgId;
    @NotNull
    @ApiModelProperty("room id")
    private Long roomId;
}
