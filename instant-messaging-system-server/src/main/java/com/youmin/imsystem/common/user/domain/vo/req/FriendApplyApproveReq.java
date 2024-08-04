package com.youmin.imsystem.common.user.domain.vo.req;

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
public class FriendApplyApproveReq {

    @NotNull
    @ApiModelProperty("User Apply Id")
    private Long applyId;
}
