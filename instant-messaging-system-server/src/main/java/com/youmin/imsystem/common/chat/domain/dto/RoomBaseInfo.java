package com.youmin.imsystem.common.chat.domain.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
public class RoomBaseInfo {

    private Long roomId;

    @ApiModelProperty("room name")
    private String name;

    @ApiModelProperty("room avatar url")
    private String avatar;

    @ApiModelProperty("Room Type")
    /**
     * @see com.youmin.imsystem.common.chat.domain.enums.RoomTypeEnums
     */
    private Integer type;

    private Integer hot_flag;

    private Date activeTime;

    private Long lastMsgId;
}
