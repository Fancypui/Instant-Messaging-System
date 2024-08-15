package com.youmin.imsystem.common.user.cache;

import com.youmin.imsystem.common.common.constant.RedisConstant;
import com.youmin.imsystem.common.user.dao.BlackDao;
import com.youmin.imsystem.common.user.dao.UserRoleDao;
import com.youmin.imsystem.common.user.domain.entity.Black;
import com.youmin.imsystem.common.user.domain.entity.User;
import com.youmin.imsystem.common.user.domain.entity.UserRole;
import com.youmin.imsystem.common.utils.RedisUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class UserCache {

    @Autowired
    private UserRoleDao userRoleDao;


    @Autowired
    private BlackDao blackDao;

    @Autowired
    private UserInfoCache userInfoCache;

    @Autowired
    private UserSummaryCache userSummaryCache;

    public void userInfoChange(Long uid){
        userInfoCache.delete(uid);
        userSummaryCache.delete(uid);
        refreshModifyTime(uid);
    }

    private void refreshModifyTime(Long uid) {
        String key = RedisConstant.getKey(RedisConstant.USER_MODIFY_STRING, uid);
        RedisUtils.set(key,new Date().getTime());
    }


    @Cacheable(cacheNames = "user", key = "'roles'+#uid")
    public Set<Long> getUserRole(Long uid){
        List<UserRole> userRole = userRoleDao.listByUid(uid);
        return userRole.stream().map(UserRole::getRoleId).collect(Collectors.toSet());
    }

    @Cacheable(cacheNames = "user", key = "'blackList'")
    public Map<Integer,Set<String>> blackMap(){
        Map<Integer, List<Black>> filter = blackDao.list().stream().collect(Collectors.groupingBy(Black::getType));
        Map<Integer, Set<String>> result = new HashMap<>();
        filter.forEach((key,value)->{
            Set<String> blackRecord = value.stream().map(Black::getTarget).collect(Collectors.toSet());
            result.put(key,blackRecord);
        });
        return result;
    }

    @CacheEvict(cacheNames = "user", key = "'blackList'")
    public Map<Integer,Set<String>> evictBlackMap(){
        return null;
    }

    public List<Long> getUserModifyTime(List<Long> uidList) {
        List<String> keys = uidList.stream().
                map(uid -> {
                    return RedisConstant.getKey(RedisConstant.USER_MODIFY_STRING, uid);
                }).collect(Collectors.toList());
        return RedisUtils.mget(keys,Long.class);
    }


}
