package com.youmin.imsystem.common.chat.service.cache;

import cn.hutool.core.lang.Pair;
import com.youmin.imsystem.common.common.constant.RedisConstant;
import com.youmin.imsystem.common.common.domain.vo.req.CursorBaseReq;
import com.youmin.imsystem.common.common.domain.vo.resp.CursorPageBaseResp;
import com.youmin.imsystem.common.common.utils.CursorUtils;
import com.youmin.imsystem.common.utils.RedisUtils;

import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Set;

@Component
public class HotRoomCache {

    public Set<ZSetOperations.TypedTuple<String>> getRoomRange(Double hotStart, Double hotEnd) {
        return RedisUtils.zRangeByScoreWithScores(RedisConstant.getKey(RedisConstant.HOT_ROOM_ZSET), hotStart, hotEnd);
    }

    public CursorPageBaseResp<Pair<Long,Double>> getRoomCursorPage(CursorBaseReq request) {
         return CursorUtils.getCursorPageByRedis(RedisConstant.getKey(RedisConstant.HOT_ROOM_ZSET),request,Long::parseLong);
    }

    public void refreshActiveTime(Long roomId, Date refreshTime){
        RedisUtils.zAdd(RedisConstant.getKey(RedisConstant.HOT_ROOM_ZSET),roomId,(double)refreshTime.getTime());
    }



}
