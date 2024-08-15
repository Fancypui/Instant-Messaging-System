package com.youmin.imsystem.common.user.domain.vo.resp;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WSFriendApply {

    @ApiModelProperty("UID who apply")
    private Long uid;

    @ApiModelProperty("Friend Request unread count")
    private Integer unreadCount;
}
