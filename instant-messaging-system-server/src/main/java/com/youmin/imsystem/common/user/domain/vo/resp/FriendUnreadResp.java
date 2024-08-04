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
public class FriendUnreadResp {

    @ApiModelProperty("User Apply Unread count")
    private Integer unreadCount;

}
