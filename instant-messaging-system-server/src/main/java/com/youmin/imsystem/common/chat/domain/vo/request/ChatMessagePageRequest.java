package com.youmin.imsystem.common.chat.domain.vo.request;

import com.youmin.imsystem.common.common.domain.vo.req.CursorBaseReq;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessagePageRequest extends CursorBaseReq {

    @NotNull
    @ApiModelProperty("sessionId")
    private Long roomId;
}
