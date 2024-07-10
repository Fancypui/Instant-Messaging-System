package com.youmin.imsystem.websocket.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@AllArgsConstructor
@Getter
public enum WSRespTypeEnum {

    LOGIN_URL(1,"Return Login QRCode"),
    LOGIN_SCAN_SUCCESS(2,"User scan success, waiting for authentication"),
    LOGIN_SUCcESS(3,"User login sucess, returning user infomation"),
    MESSAGE(4,"New Messgae"),
    ONLINE_OFFLINE_NOTIFY(5,"Online and offline notification"),
    INVALIDATE_TOKEN(6,"Make client's token invalid, which means client needs to relogin"),
    BLACK(7,"Block user"),
    MARK(8,"Message mark"),
    RECALL(9,"Message recall"),
    APPLY(10,"Freind request"),
    MEMBER_CHANGE(11, "Member change");

    private final Integer type;
    private final String desc;

    private static Map<Integer, WSRespTypeEnum> cache;

    static{
        cache = Arrays.stream(WSRespTypeEnum.values()).collect(Collectors.toMap(WSRespTypeEnum::getType, Function.identity()));
    }
    public static WSRespTypeEnum of(Integer type){
        return cache.get(type);
    }

}
