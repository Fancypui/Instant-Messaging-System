package com.youmin.imsystem.common.chat.service.strategy.msg;

import com.youmin.imsystem.common.chat.dao.MessageDao;
import com.youmin.imsystem.common.chat.domain.entity.Message;
import com.youmin.imsystem.common.chat.domain.entity.msg.MessageExtra;
import com.youmin.imsystem.common.chat.domain.enums.MessageTypeEnum;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class SystemMsgHandler extends AbstractMsgHandler<String>{

    @Autowired
    private MessageDao messageDao;

    @Override
    public Object showMsg(Message msg) {
        return msg.getContent();
    }

    @Override
    public Object showReplyMsg(Message msg) {
        return msg.getContent();
    }

    @Override
    protected void saveMsg(Message message, String body) {
        Message update = new Message();
        update.setId(message.getId());
        update.setContent(body);
        messageDao.updateById(update);

    }

    @Override
    MessageTypeEnum getMsgTypeEnum() {
        return MessageTypeEnum.SYSTEM;
    }

    @Override
    public String showContactMsg(Message msg) {
        return msg.getContent();
    }
}
