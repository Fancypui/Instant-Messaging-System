package com.youmin.imsystem.common.common.utils;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.youmin.imsystem.common.common.domain.vo.req.CursorBaseReq;
import com.youmin.imsystem.common.common.domain.vo.resp.CursorPageBaseResp;
import com.youmin.imsystem.common.user.domain.entity.UserFriend;

import java.util.Date;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Cursor Pagination Utils
 */
public class CursorUtils {


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
