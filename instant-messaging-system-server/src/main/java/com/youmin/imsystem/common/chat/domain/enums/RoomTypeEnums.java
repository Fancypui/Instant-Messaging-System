package com.youmin.imsystem.common.chat.domain.enums;

import io.swagger.models.auth.In;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum RoomTypeEnums {

    FRIEND_ROOM(2,"One-to-One Friend Chat Room"),
    Group_Room(1,"Group chat");

    private final Integer roomType;
    private final String desc;

}
