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
public class MessageMarkReq {

    @ApiModelProperty("Message id")
    @NotNull
    private Long msgId;

    @ApiModelProperty("mark type 1 like, 2 dislike")
    @NotNull
    private Integer markType;

    @ApiModelProperty("act type 1 confirm, 2 cancel")
    @NotNull
    private Integer actType;
}
