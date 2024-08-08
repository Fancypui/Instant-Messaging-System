package com.youmin.imsystem.common.common.constant;

public class RedisConstant {
    public static final String BASE = "IMSYSTEM:CHAT";

    public static final String USER_TOKEN = "userToken:uid_%d";

    public static final String USER_MODIFY_STRING = "userModify:uid_%d";

    public static final String USER_INFO_STRING = "userInfo:uid_%d";

    public static final String USER_SUMMARY_STRING = "userSummary:uid_%d";

    public static final String ROOM_INFO_STRING = "roomInfo:id_%d";
    public static final String GROUP_INFO_STRING = "groupInfo:id_%d";

    public static String getKey(String key, Object... obj){
        return String.format(key,obj);
    }
}
