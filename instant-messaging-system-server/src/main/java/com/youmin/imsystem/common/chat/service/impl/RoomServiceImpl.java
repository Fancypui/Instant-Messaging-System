package com.youmin.imsystem.common.chat.service.impl;

import com.youmin.imsystem.common.chat.dao.RoomDao;
import com.youmin.imsystem.common.chat.dao.RoomFriendDao;
import com.youmin.imsystem.common.chat.domain.entity.Room;
import com.youmin.imsystem.common.chat.domain.entity.RoomFriend;
import com.youmin.imsystem.common.chat.enums.RoomTypeEnums;
import com.youmin.imsystem.common.chat.service.IRoomService;
import com.youmin.imsystem.common.chat.service.adapter.ChatAdapter;
import com.youmin.imsystem.common.common.domain.enums.NormalOrNotEnum;
import com.youmin.imsystem.common.common.utils.AssertUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
public class RoomServiceImpl implements IRoomService {

    @Autowired
    private RoomFriendDao roomFriendDao;

    @Autowired
    private RoomDao roomDao;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RoomFriend createFriendRoom(List<Long> uidList) {
        AssertUtils.isNotEmpty(uidList,"Cannot create friend room, no user provided");
        AssertUtils.equal(uidList.size(),2,"Cannot create friend room; no of user is not enough to create a friend chat room");
        String roomKey = ChatAdapter.generateKey(uidList);

        RoomFriend roomFriend = roomFriendDao.getRoomByRoomKey(roomKey);
        if(Objects.nonNull(roomFriend)){//restore room if it is not empty (scenario where users delete each other but now restore back)
            restoreRoomIfNeed(roomFriend);
        }else{
            Room room = createRoom(RoomTypeEnums.FRIEND_ROOM);
            roomFriend = createFriendRoom(room.getId(),uidList);
        }
        return roomFriend;
    }

    @Override
    public void disableFriendRoom(List<Long> uidList) {
        AssertUtils.isNotEmpty(uidList,"Cannot disable friend room, no user provided");
        AssertUtils.equal(uidList.size(),2,"Cannot disable friend room; no of user is not enough to create a friend chat room");
        String roomKey = ChatAdapter.generateKey(uidList);
        roomFriendDao.disableRoom(roomKey);

    }

    private Room createRoom(RoomTypeEnums roomTypeEnums) {
        Room insert = ChatAdapter.buildRoom(roomTypeEnums);
        roomDao.save(insert);
        return insert;
    }

    private RoomFriend createFriendRoom(Long roomId,List<Long> uidList){
        RoomFriend insert = ChatAdapter.buildFriendRoom(roomId, uidList);
        roomFriendDao.save(insert);
        return insert;
    }

    private void restoreRoomIfNeed(RoomFriend roomFriend) {
        if(Objects.equals(roomFriend.getStatus(), NormalOrNotEnum.NOT_NORMAL.getStatus())){
            roomFriendDao.restoreRoom(roomFriend.getId());
        }
    }
}
