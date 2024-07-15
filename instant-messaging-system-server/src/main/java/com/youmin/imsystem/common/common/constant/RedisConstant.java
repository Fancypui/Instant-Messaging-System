package com.youmin.imsystem.common.common.constant;

public class RedisConstant {
    public static final String BASE = "IMSYSTEM:CHAT";

    public static final String USER_TOKEN = "userToken:uid_%d";

    public static String getKey(String key, Object... obj){
        return String.format(key,obj);
    }
}
