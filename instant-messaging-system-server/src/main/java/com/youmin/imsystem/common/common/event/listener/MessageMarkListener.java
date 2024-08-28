package com.youmin.imsystem.common.common.event.listener;

import com.youmin.imsystem.common.chat.dao.MessageMarkDao;
import com.youmin.imsystem.common.chat.domain.dto.ChatMessageMarkDTO;
import com.youmin.imsystem.common.common.event.MessageMarkEvent;
import com.youmin.imsystem.common.user.service.adapter.WSAdapter;
import com.youmin.imsystem.common.user.service.impl.PushService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class MessageMarkListener {

    @Autowired
    private MessageMarkDao messageMarkDao;

    @Autowired
    private PushService pushService;

    @Async
    @TransactionalEventListener(classes = MessageMarkEvent.class,fallbackExecution = true)
    public void notifyAll(MessageMarkEvent event){
        ChatMessageMarkDTO dto = event.getDto();
        Integer markCount = messageMarkDao.getMarkCount(dto.getMsgId(),dto.getMarkType());
        pushService.pushService(WSAdapter.buildMsgMarkSend(dto,markCount));
    }
}
