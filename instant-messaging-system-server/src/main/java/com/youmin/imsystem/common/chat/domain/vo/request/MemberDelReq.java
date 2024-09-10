package com.youmin.imsystem.common.chat.domain.vo.request;

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
public class MemberDelReq {
    @NotNull
    @ApiModelProperty("room id")
    private Long roomId;

    @NotNull
    @ApiModelProperty("removed uid")
    private Long uid;
}
