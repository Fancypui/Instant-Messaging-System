package com.youmin.imsystem.common.common.utils;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.youmin.imsystem.common.chat.domain.entity.Room;
import com.youmin.imsystem.common.common.domain.vo.req.CursorBaseReq;
import com.youmin.imsystem.common.common.domain.vo.resp.CursorPageBaseResp;
import com.youmin.imsystem.common.user.domain.entity.UserFriend;
import com.youmin.imsystem.common.utils.RedisUtils;
import cn.hutool.core.lang.Pair;
import org.springframework.data.redis.core.ZSetOperations;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Cursor Pagination Utils
 */
public class CursorUtils {

    public static <T> CursorPageBaseResp<Pair<T,Double>> getCursorPageByRedis(String key, CursorBaseReq request, Function<String,T> typeConvert) {
        Set<ZSetOperations.TypedTuple<String>> typedTuples;
        if(Objects.isNull(request.getCursor())){//first time
            typedTuples = RedisUtils.zReverseRangeWithScores(key, request.getPageSize());
        }else{
            typedTuples = RedisUtils.zReverseRangeByScoreWithScores(key,Double.parseDouble(request.getCursor()),request.getPageSize());
        }
        List<Pair<T, Double>> result = typedTuples
                .stream()
                .map(a ->
                        Pair.of(typeConvert.apply(a.getValue()), a.getScore())
                )
                .sorted((o1, o2) -> o2.getValue().compareTo(o1.getValue()))
                .collect(Collectors.toList());
        String cursor = Optional.ofNullable(CollectionUtil.getLast(result))
                .map(Pair::getValue)
                .map(String::valueOf)
                .orElse(null);
        boolean isLast = result.size() != request.getPageSize();
        return new CursorPageBaseResp<>(cursor,isLast,result);



    }

    public static <T> CursorPageBaseResp<T> getCursorPageByMysql(IService<T> mapper, CursorBaseReq cursorBaseReq,
                                                                 Consumer<LambdaQueryWrapper<T>> queryConditionInit,
                                                                 SFunction<T,?> cursorColumn

                                                                 ){
        /**
         * get cursor's column class type
         */
        Class<?> cursorType = LambdaUtils.getReturnType(cursorColumn);
        LambdaQueryWrapper<T> wrapper = new LambdaQueryWrapper<>();
        /**
         * let invoker to set up their query condition
         */
        queryConditionInit.accept(wrapper);
        if(StringUtils.isNotBlank(cursorBaseReq.getCursor())){
            wrapper.lt(cursorColumn,parseCursor(cursorBaseReq.getCursor(), cursorType));
        }
        wrapper.orderByDesc(cursorColumn);
        Page<T> page = mapper.page(cursorBaseReq.plusPage(), wrapper);
        /**
         * to find out if current cursor page is the last page
         */
        boolean isLast = page.getRecords().size() != cursorBaseReq.getPageSize();
        /**
         * extract  cursor value
         */
        String cursor = Optional.ofNullable(CollectionUtil.getLast(page.getRecords()))
                .map(cursorColumn)
                .map(CursorUtils::toCursor)
                .orElse(null);
        return new CursorPageBaseResp<>(cursor,isLast,page.getRecords());


    }

    public static <T> String toCursor(Object o){
        if(o instanceof Date){
            return String.valueOf(((Date)o).getTime());
        }
        return o.toString();
    }

    public static Object parseCursor(String cursor, Class<?> cursorClass){
        if(Date.class.isAssignableFrom(cursorClass)){
            return new Date(Long.parseLong(cursor));
        }
        return cursor;
    }


}
