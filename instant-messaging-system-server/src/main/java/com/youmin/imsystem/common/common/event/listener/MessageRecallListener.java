package com.youmin.imsystem.common.common.event.listener;

import com.youmin.imsystem.common.chat.domain.dto.MsgRecallDTO;
import com.youmin.imsystem.common.chat.service.cache.MsgCache;
import com.youmin.imsystem.common.common.constant.MQConstant;
import com.youmin.imsystem.common.common.domain.dto.MsgSendMessageDTO;
import com.youmin.imsystem.common.common.event.MessageSendEvent;
import com.youmin.imsystem.common.common.event.MsgRecallEvent;
import com.youmin.imsystem.common.user.service.adapter.WSAdapter;
import com.youmin.imsystem.common.user.service.impl.PushService;
import com.youmin.imsystem.transaction.service.MQProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class MessageRecallListener {

    @Autowired
    private PushService pushService;

    @Autowired
    private MsgCache msgCache;


    @TransactionalEventListener(classes = MsgRecallEvent.class,fallbackExecution = true)
    public void clearMsg(MsgRecallEvent event){
        MsgRecallDTO msgRecallDTO = event.getMsgRecallDTO();
        msgCache.evictMsg(msgRecallDTO.getMsgId());
    }


    @TransactionalEventListener(classes = MsgRecallEvent.class,fallbackExecution = true)
    public void sendToAll(MsgRecallEvent event){
        MsgRecallDTO msgRecallDTO = event.getMsgRecallDTO();
        pushService.pushService(WSAdapter.buildMsgRecall(msgRecallDTO));
    }
}
