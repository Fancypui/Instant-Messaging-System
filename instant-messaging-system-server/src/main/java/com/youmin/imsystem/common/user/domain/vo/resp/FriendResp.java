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
public class FriendResp {

    @ApiModelProperty("friend uid")
    private Long uid;
    @ApiModelProperty("friend name")
    private String name;
    @ApiModelProperty("Friend avatar url")
    private String avatar;
    /**
     * @see com.youmin.imsystem.common.user.enums.ChatActiveStatusEnum
     */
    @ApiModelProperty("Friend Active Status, 0 online, 1 offline")
    private Integer activeStatus;
}
