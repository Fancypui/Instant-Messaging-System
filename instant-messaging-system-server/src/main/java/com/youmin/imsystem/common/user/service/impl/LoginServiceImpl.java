package com.youmin.imsystem.common.user.service.impl;

import com.youmin.imsystem.common.common.constant.RedisConstant;
import com.youmin.imsystem.common.common.utils.JWTUtils;
import com.youmin.imsystem.common.user.service.LoginService;
import com.youmin.imsystem.common.utils.RedisUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Service
public class LoginServiceImpl implements LoginService {

    @Autowired
    private JWTUtils jwtUtils;

    //if token's expire day is less than 1, then renew
    private static final int TOKEN_RENEWAL_TIME = 1;
    private static final int TOKEN_VALID_DURATION = 3;

    @Override
    public String login(Long uid) {
        String key = RedisConstant.getKey(RedisConstant.USER_TOKEN, uid);
        String token = RedisUtils.getStr(key);
        //to adapt to scenario where user login from multiple devices (mobile web, laptop.....)
        if(Objects.nonNull(token)){
            return token;
        }
        token = jwtUtils.createToken(uid);
        RedisUtils.set(getUserTokenKey(uid),token,TOKEN_VALID_DURATION, TimeUnit.DAYS);
        return token;
    }

    @Override
    @Async
    public void renewalTokenIfNecessary(String token) {
        Long uid = getValidUid(token);
        if(uid==null){
            return;
        }
        Long expireDays = RedisUtils.getExpire(getUserTokenKey(uid),TimeUnit.DAYS);
        //Redis does not have this value
        if(expireDays==-2){
            return;
        }
        if(expireDays<=TOKEN_RENEWAL_TIME){
            RedisUtils.expire(getUserTokenKey(uid),TOKEN_VALID_DURATION, TimeUnit.DAYS);
        }
    }

    @Override
    public Long getValidUid(String token) {
        Long uid = jwtUtils.getUidOrNull(token);
        if(Objects.isNull(uid)){
            return null;
        }
        String oldToken = RedisUtils.get(getUserTokenKey(uid), String.class);
        if(StringUtils.isBlank(oldToken) ){
            return null;
        }
        return Objects.equals(token,oldToken)?uid:null;

    }


    private String getUserTokenKey(Long uid){
        return RedisConstant.getKey(RedisConstant.USER_TOKEN,uid);
    }

}
