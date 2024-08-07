package com.youmin.imsystem.common.user.cache;

import cn.hutool.cache.impl.AbstractCache;
import com.youmin.imsystem.common.common.constant.RedisConstant;
import com.youmin.imsystem.common.common.service.cache.AbstractRedisStringCache;
import com.youmin.imsystem.common.user.dao.UserDao;
import com.youmin.imsystem.common.user.domain.entity.User;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * user essential info cache
 */
@Component
public class UserInfoCache extends AbstractRedisStringCache<Long, User> {

    @Autowired
    private UserDao userDao;

    @Override
    public Long expire() {
        return 10*60L;
    }

    @Override
    public String getKey(Long uid) {
        return RedisConstant.getKey(RedisConstant.USER_INFO_STRING,uid);
    }

    /**
     * load from database if redis does not have the value (logic)
     * @param keys
     * @return
     */
    @Override
    public Map<Long, User> load(List<Long> keys) {
        List<User> needLoadUserList = userDao.listByIds(keys);
        return needLoadUserList.stream().collect(Collectors.toMap(User::getId, Function.identity()));
    }
}
