package com.youmin.imsystem.common.chat.service.strategy.msg;

import com.youmin.imsystem.common.common.exception.CommonErrorEnum;
import com.youmin.imsystem.common.common.utils.AssertUtils;
import io.swagger.models.auth.In;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class MsgHandlerFactory {

    private static final Map<Integer,AbstractMsgHandler> STRATEGY_MAP = new HashMap<>();

    public static void register(Integer code,AbstractMsgHandler handler){
        STRATEGY_MAP.put(code,handler);
    }

    public static AbstractMsgHandler getStrategyOrNull(Integer code){
        AbstractMsgHandler strategy = STRATEGY_MAP.get(code);
        AssertUtils.isNotEmpty(strategy, CommonErrorEnum.PARAM_VALID);
        return strategy;
    }
}
