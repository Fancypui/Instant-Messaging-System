package com.youmin.imsystem.common.chat.service.cache;

import com.youmin.imsystem.common.chat.dao.RoomDao;
import com.youmin.imsystem.common.chat.dao.RoomGroupDao;
import com.youmin.imsystem.common.chat.domain.entity.Room;
import com.youmin.imsystem.common.chat.domain.entity.RoomGroup;
import com.youmin.imsystem.common.common.constant.RedisConstant;
import com.youmin.imsystem.common.common.service.cache.AbstractRedisStringCache;
import com.youmin.imsystem.common.common.utils.RedisUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class RoomCache extends AbstractRedisStringCache<Long,Room> {

    @Autowired
    private RoomDao roomDao;

    @Override
    public Long expire() {
        return 5L*60;
    }

    @Override
    public Map<Long, Room> load(List<Long> keys) {
        return roomDao
                .listByIds(keys)
                .stream()
                .collect(Collectors.toMap(Room::getId, Function.identity()));
    }

    @Override
    public String getKey(Long id) {
        return RedisConstant.getKey(RedisConstant.ROOM_INFO_STRING,id);
    }
}
