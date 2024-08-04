package com.youmin.imsystem.common.user.domain.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class FriendDeleteReq {

    @NotNull
    @ApiModelProperty("Friend id that current user want to delete")
    private Long friendUid;
}
