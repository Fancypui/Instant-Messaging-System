package com.youmin.imsystem.common.common.event.listener;

import com.youmin.imsystem.common.common.event.MessageSendEvent;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class MessageSendListener {

    @TransactionalEventListener(classes = MessageSendEvent.class,fallbackExecution = true,phase = TransactionPhase.BEFORE_COMMIT)
    public void messageRoute(MessageSendEvent event){
        //todo publish to mq
    }
}
