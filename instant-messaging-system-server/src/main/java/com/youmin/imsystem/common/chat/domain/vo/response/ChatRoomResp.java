package com.youmin.imsystem.common.chat.domain.vo.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatRoomResp {
    @ApiModelProperty("room id")
    private Long roomId;
    @ApiModelProperty("room type")
    private Integer type;

    @ApiModelProperty("")
    private Integer hot_flag;
    @ApiModelProperty("latest message")
    private String text;

    @ApiModelProperty("room name")
    private String name;

    @ApiModelProperty("room avatar url")
    private String avatar;

    @ApiModelProperty("latest message sent time")
    private Date activeTime;

    @ApiModelProperty("room unread count")
    private Integer unreadCount;
}
