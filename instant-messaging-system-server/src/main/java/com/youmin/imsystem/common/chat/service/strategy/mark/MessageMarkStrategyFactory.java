package com.youmin.imsystem.common.chat.service.strategy.mark;


import com.youmin.imsystem.common.chat.service.strategy.msg.AbstractMsgHandler;
import com.youmin.imsystem.common.common.exception.CommonErrorEnum;
import com.youmin.imsystem.common.common.utils.AssertUtils;

import java.util.HashMap;
import java.util.Map;

public class MessageMarkStrategyFactory {

    private static final Map<Integer,AbstractMessageMark> messageMarkStrategyMap = new HashMap<>();

    public static void register(Integer messageMarkType, AbstractMessageMark strategy){
        messageMarkStrategyMap.put(messageMarkType,strategy);
    }

    public static AbstractMessageMark getStrategyOrNull(Integer messageMarkType){
        AbstractMessageMark messageMarkStrategy = messageMarkStrategyMap.get(messageMarkType);
        AssertUtils.isNotEmpty(messageMarkStrategy, CommonErrorEnum.PARAM_VALID);
        return messageMarkStrategy;
    }

}
