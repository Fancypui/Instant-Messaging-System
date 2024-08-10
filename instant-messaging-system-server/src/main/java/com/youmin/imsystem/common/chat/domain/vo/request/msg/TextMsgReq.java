package com.youmin.imsystem.common.chat.domain.vo.request.msg;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TextMsgReq {

    @NotBlank(message = "message content cannot be blank")
    @Size(max = 1024,message = "message content too long")
    @ApiModelProperty("message content")
    private String content;

    @ApiModelProperty("roomId")
    private Long replyMsgId;

    @ApiModelProperty("tag uid list")
    @Size(max = 10,message = "cannot tag so many people at one time")
    private List<Long> atUidList;

}
