package com.youmin.imsystem.common.chat.enums;

import io.swagger.models.auth.In;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum RoomTypeEnums {

    FRIEND_ROOM(1,"One-to-One Friend Chat Room"),
    Group_Room(2,"Group chat");

    private final Integer roomType;
    private final String desc;

}
