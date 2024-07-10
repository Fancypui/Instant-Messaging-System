package com.youmin.imsystem.websocket.domain.enums;


import com.youmin.imsystem.websocket.domain.vo.req.WSReqBase;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@AllArgsConstructor
@Getter
public enum WSReqTypeEnum {

    LOGIN(1,"Request for QR login"),
    HEARTBEAT(2,"Heartbeat test"),
    AUTHORIZATION(3,"Login authentication");

    private final Integer type;
    private final String desc;
    private static Map<Integer, WSReqTypeEnum> cache;
    static{
        cache = Arrays.stream(WSReqTypeEnum.values()).collect(Collectors.toMap(WSReqTypeEnum::getType, Function.identity()));
    }
    public static WSReqTypeEnum of(Integer type){
        return cache.get(type);
    }

}
