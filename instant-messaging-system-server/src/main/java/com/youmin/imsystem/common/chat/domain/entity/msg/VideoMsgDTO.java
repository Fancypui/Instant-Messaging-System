package com.youmin.imsystem.common.chat.domain.entity.msg;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class VideoMsgDTO extends BaseFileDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    @ApiModelProperty("thumbWidth")
    @NotNull
    private Integer thumbWidth;

    @ApiModelProperty("thumbHeight")
    @NotNull
    private Integer thumbHeight;

    @ApiModelProperty("thumbSize")
    @NotNull
    private Long thumbSize;

    @ApiModelProperty("thumburl")
    @NotBlank
    private String thumbUrl;
}
