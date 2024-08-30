package com.youmin.imsystem.common.chat.domain.vo.request;

import com.youmin.imsystem.common.common.domain.vo.req.CursorBaseReq;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessageReadPageReq extends CursorBaseReq {

    @ApiModelProperty("msg id")
    @NotNull
    private Long msgId;

    @ApiModelProperty("Search Type 1 read 2 unread")
    @NotNull
    private Long searchType;
}
