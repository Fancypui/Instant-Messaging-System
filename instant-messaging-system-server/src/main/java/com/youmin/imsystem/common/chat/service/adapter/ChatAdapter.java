package com.youmin.imsystem.common.chat.service.adapter;

import com.youmin.imsystem.common.chat.domain.entity.Room;
import com.youmin.imsystem.common.chat.domain.entity.RoomFriend;
import com.youmin.imsystem.common.chat.domain.enums.HotFlagEnum;
import com.youmin.imsystem.common.chat.domain.enums.RoomTypeEnums;
import com.youmin.imsystem.common.common.domain.enums.NormalOrNotEnum;

import java.util.List;
import java.util.stream.Collectors;

public class ChatAdapter {

    private static final String COMMA_SEPARATOR = ",";

    public static String generateKey(List<Long> uidList){
        return uidList
                .stream()
                .sorted()
                .map(String::valueOf)
                .collect(Collectors.joining(COMMA_SEPARATOR));

    }

    public static Room buildRoom(RoomTypeEnums roomTypeEnums){
        Room room = new Room();
        room.setType(roomTypeEnums.getRoomType());
        room.setHotFlag(HotFlagEnum.NOT.getType());
        return room;
    }


    public static RoomFriend buildFriendRoom(Long roomId, List<Long> uidList) {
        RoomFriend roomFriend = new RoomFriend();
        roomFriend.setRoomId(roomId);
        roomFriend.setRoomKey(generateKey(uidList));
        List<Long> sortedUidList = uidList.stream().sorted().collect(Collectors.toList());
        roomFriend.setUid1(sortedUidList.get(0));
        roomFriend.setUid2(sortedUidList.get(1));
        roomFriend.setStatus(NormalOrNotEnum.NORMAL.getStatus());
        return roomFriend;
    }
}
