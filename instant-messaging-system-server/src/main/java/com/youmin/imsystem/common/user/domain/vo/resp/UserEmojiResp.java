package com.youmin.imsystem.common.user.domain.vo.resp;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserEmojiResp {

    @ApiModelProperty("Emoji id")
    private Long id;

    @ApiModelProperty("Emoji url")
    private String expressionUrl;
}
