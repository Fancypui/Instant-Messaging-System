package com.youmin.imsystem.common.user.domain.vo.req;

import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FriendApplyReq {

    @NotNull
    @ApiModelProperty("Uid that current user want to send friend request")
    private Long targetUid;

    @NotBlank
    @ApiModelProperty("Remark during sending friend request")
    private String msg;
}
