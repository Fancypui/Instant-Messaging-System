package com.youmin.imsystem.common.chat.domain.enums;

import io.swagger.models.auth.In;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@AllArgsConstructor
@Getter
public enum RoomTypeEnums {

    FRIEND_ROOM(2,"One-to-One Friend Chat Room"),
    Group_Room(1,"Group chat");

    private final Integer roomType;
    private final String desc;
    private static Map<Integer, RoomTypeEnums> cache;

    static {
        cache = Arrays.stream(RoomTypeEnums.values()).collect(Collectors.toMap(RoomTypeEnums::getRoomType, Function.identity()));
    }

    public static RoomTypeEnums of(Integer type) {
        return cache.get(type);
    }
}
