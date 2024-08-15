package com.youmin.imsystem.common.common.event.listener;

import com.youmin.imsystem.common.common.constant.MQConstant;
import com.youmin.imsystem.common.common.domain.dto.MsgSendMessageDTO;
import com.youmin.imsystem.common.common.event.MessageSendEvent;
import com.youmin.imsystem.transaction.service.MQProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class MessageSendListener {

    @Autowired
    private MQProducer mqProducer;

    @TransactionalEventListener(classes = MessageSendEvent.class,fallbackExecution = true,phase = TransactionPhase.BEFORE_COMMIT)
    public void messageRoute(MessageSendEvent event){
        Long msgId = event.getMsgId();
        mqProducer.sendMsg(MQConstant.SEND_MSG_EXCHANGE,MQConstant.SEND_MSG_ROUTING_KEY, new MsgSendMessageDTO(msgId));
    }
}
