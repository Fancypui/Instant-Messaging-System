package com.youmin.imsystem.common.chat.service.cache;

import com.youmin.imsystem.common.common.constant.RedisConstant;
import com.youmin.imsystem.common.utils.RedisUtils;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class HotRoomCache {


    public void refreshActiveTime(Long roomId, Date refreshTime){
        RedisUtils.zAdd(RedisConstant.getKey(RedisConstant.HOT_ROOM_ZSET),roomId,(double)refreshTime.getTime());
    }
}
