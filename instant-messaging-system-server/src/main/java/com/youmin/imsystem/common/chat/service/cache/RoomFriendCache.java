package com.youmin.imsystem.common.chat.service.cache;

import cn.hutool.cache.impl.AbstractCache;
import cn.hutool.cache.impl.CacheObj;
import com.youmin.imsystem.common.chat.dao.RoomFriendDao;
import com.youmin.imsystem.common.chat.dao.RoomGroupDao;
import com.youmin.imsystem.common.chat.domain.entity.RoomFriend;
import com.youmin.imsystem.common.chat.domain.entity.RoomGroup;
import com.youmin.imsystem.common.common.constant.RedisConstant;
import com.youmin.imsystem.common.common.service.cache.AbstractRedisStringCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class RoomFriendCache extends AbstractRedisStringCache<Long, RoomFriend> {
    @Autowired
    private RoomFriendDao roomFriendDao;

    @Override
    public Long expire() {
        return 5L*60;
    }

    @Override
    public Map<Long, RoomFriend> load(List<Long> roomIds) {
        List<RoomFriend> roomFriends = roomFriendDao.listByRoomIds(roomIds);
        return roomFriends.stream().collect(Collectors.toMap(RoomFriend::getRoomId,Function.identity()));
    }

    @Override
    public String getKey(Long groupId) {
        return RedisConstant.getKey(RedisConstant.GROUP_FRIEND_STRING,groupId);
    }
}
