package com.youmin.imsystem.common.user.service.impl;

import com.youmin.imsystem.common.common.constant.RedisConstant;
import com.youmin.imsystem.common.common.utils.JWTUtils;
import com.youmin.imsystem.common.common.utils.RedisUtils;
import com.youmin.imsystem.common.user.service.LoginService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Service
public class LoginServiceImpl implements LoginService {

    @Autowired
    private JWTUtils jwtUtils;

    @Override
    public String login(Long uid) {
        String token = jwtUtils.createToken(uid);
        RedisUtils.set(getUserTokenKey(uid),token,3, TimeUnit.DAYS);
        return token;
    }

    @Override
    public void renewalTokenIfNecessary(String token) {

    }

    @Override
    public Long getValidUid(String token) {
        Long uid = jwtUtils.getUidOrNull(token);
        if(Objects.isNull(uid)){
            return null;
        }
        String oldToken = RedisUtils.get(getUserTokenKey(uid), String.class);
        if(StringUtils.isBlank(oldToken)){
            return null;
        }
        return uid;

    }


    private String getUserTokenKey(Long uid){
        return RedisConstant.getKey(RedisConstant.USER_TOKEN,uid);
    }

}
