package com.youmin.imsystem.common.chat.domain.entity.msg;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EmojiMsgDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("download url")
    @NotBlank
    private String url;
}
