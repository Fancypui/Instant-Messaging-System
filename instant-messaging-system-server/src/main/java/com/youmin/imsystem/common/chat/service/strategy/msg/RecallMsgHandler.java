package com.youmin.imsystem.common.chat.service.strategy.msg;

import com.youmin.imsystem.common.chat.dao.MessageDao;
import com.youmin.imsystem.common.chat.domain.dto.MsgRecallDTO;
import com.youmin.imsystem.common.chat.domain.entity.Message;
import com.youmin.imsystem.common.chat.domain.entity.msg.MessageExtra;
import com.youmin.imsystem.common.chat.domain.entity.msg.MsgRecall;
import com.youmin.imsystem.common.chat.domain.enums.MessageTypeEnum;
import com.youmin.imsystem.common.common.event.MsgRecallEvent;
import com.youmin.imsystem.common.user.cache.UserCache;
import com.youmin.imsystem.common.user.cache.UserInfoCache;
import com.youmin.imsystem.common.user.domain.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Objects;

@Component
public class RecallMsgHandler extends AbstractMsgHandler<Object>{

    @Autowired
    private MessageDao messageDao;

    @Autowired
    private UserInfoCache userInfoCache;

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    @Override
    public Object showMsg(Message msg) {
        MsgRecall recall = msg.getExtra().getMsgRecall();
        User userInfo = userInfoCache.get(recall.getRecallUid());
        if(!Objects.equals(msg.getFromUid(),recall.getRecallUid())){
            return "Admin\"" + userInfo.getName() + "\"recall a message";
        }
        return "\"" + userInfo.getName() + "\"recall a message";
    }

    @Override
    public Object showReplyMsg(Message msg) {
        return "Message has been recall";
    }

    @Override
    protected void saveMsg(Message message, Object body) {
        throw new UnsupportedOperationException();
    }

    public void recall(Long uid, Message message){
        Message update = new Message();
        update.setId(message.getId());
        update.setType(getMsgTypeEnum().getMsgType());
        MessageExtra msgExtra = new MessageExtra();
        msgExtra.setMsgRecall(new MsgRecall(uid, new Date()));
        update.setExtra(msgExtra);
        messageDao.updateById(update);
        applicationEventPublisher.publishEvent(new MsgRecallEvent(this,
                new MsgRecallDTO(message.getRoomId(), message.getId(),uid)));
    }


    @Override
    MessageTypeEnum getMsgTypeEnum() {
        return MessageTypeEnum.RECALL;
    }
}
