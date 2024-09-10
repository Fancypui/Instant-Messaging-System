package com.youmin.imsystem.common.chat.service.impl;

import com.youmin.imsystem.common.chat.dao.GroupMemberDao;
import com.youmin.imsystem.common.chat.dao.RoomDao;
import com.youmin.imsystem.common.chat.dao.RoomFriendDao;
import com.youmin.imsystem.common.chat.dao.RoomGroupDao;
import com.youmin.imsystem.common.chat.domain.entity.GroupMember;
import com.youmin.imsystem.common.chat.domain.entity.Room;
import com.youmin.imsystem.common.chat.domain.entity.RoomFriend;
import com.youmin.imsystem.common.chat.domain.entity.RoomGroup;
import com.youmin.imsystem.common.chat.domain.enums.GroupRoleAppEnum;
import com.youmin.imsystem.common.chat.domain.enums.RoomTypeEnums;
import com.youmin.imsystem.common.chat.service.IRoomService;
import com.youmin.imsystem.common.chat.service.adapter.ChatAdapter;
import com.youmin.imsystem.common.common.annotation.RedissonLock;
import com.youmin.imsystem.common.common.domain.enums.NormalOrNotEnum;
import com.youmin.imsystem.common.common.utils.AssertUtils;
import com.youmin.imsystem.common.user.cache.UserInfoCache;
import com.youmin.imsystem.common.user.domain.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Service
public class RoomServiceImpl implements IRoomService {

    @Autowired
    private RoomFriendDao roomFriendDao;

    @Autowired
    private RoomDao roomDao;

    @Autowired
    private GroupMemberDao groupMemberDao;

    @Autowired
    private UserInfoCache userInfoCache;

    @Autowired
    private RoomGroupDao roomGroupDao;

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

    @Override
    public RoomFriend getFriendRoom(Long uid, Long friendUid) {
        String friendRoomKey = ChatAdapter.generateKey(Arrays.asList(uid, friendUid));
        return roomFriendDao.getRoomByRoomKey(friendRoomKey);
    }

    @Override
    @Transactional
    @RedissonLock(key = "#uid")
    public RoomGroup createGroupRoom(Long uid) {
        List<GroupMember> selfGroup = groupMemberDao.getSelfGroup(uid);
        AssertUtils.isEmpty(selfGroup,"Every user can only create one group");
        Room room = createRoom(RoomTypeEnums.Group_Room);
        User user = userInfoCache.get(uid);
        //insert room group
        RoomGroup roomGroup = ChatAdapter.buildGroupRoom(user, room.getId());
        roomGroupDao.save(roomGroup);
        //insert group owner
        GroupMember leader = GroupMember.builder()
                .groupId(roomGroup.getId())
                .role(GroupRoleAppEnum.LEADER.getType())
                .uid(uid)
                .build();
        groupMemberDao.save(leader);
        return roomGroup;
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
