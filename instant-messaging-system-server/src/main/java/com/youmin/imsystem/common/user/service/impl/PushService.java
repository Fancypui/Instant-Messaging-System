package com.youmin.imsystem.common.user.service.impl;

import com.youmin.imsystem.common.common.constant.MQConstant;
import com.youmin.imsystem.common.common.domain.dto.PushMessageDTO;
import com.youmin.imsystem.transaction.service.MQProducer;
import com.youmin.imsystem.common.user.domain.vo.resp.WSRespBase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PushService {

    @Autowired
    private MQProducer mqProducer;

    //push message to specific online users group (provided in uidList)
    public void pushService(WSRespBase<?> wsRespBase, List<Long> uidList){
        mqProducer.sendMsg(MQConstant.PUSH_MSG_EXCHANGE,"", new PushMessageDTO(wsRespBase,uidList));
    }

    //push message to all online users
    public void pushService(WSRespBase<?> wsRespBase){
        mqProducer.sendMsg(MQConstant.PUSH_MSG_EXCHANGE,"", new PushMessageDTO(wsRespBase));
    }
}
