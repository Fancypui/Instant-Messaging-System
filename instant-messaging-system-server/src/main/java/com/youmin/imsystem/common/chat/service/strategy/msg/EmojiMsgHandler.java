package com.youmin.imsystem.common.chat.service.strategy.msg;

import com.youmin.imsystem.common.chat.dao.MessageDao;
import com.youmin.imsystem.common.chat.domain.entity.Message;
import com.youmin.imsystem.common.chat.domain.entity.msg.EmojiMsgDTO;
import com.youmin.imsystem.common.chat.domain.entity.msg.MessageExtra;
import com.youmin.imsystem.common.chat.domain.enums.MessageTypeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class EmojiMsgHandler extends AbstractMsgHandler<EmojiMsgDTO> {
    @Autowired
    private MessageDao messageDao;

    @Override
    public Object showMsg(Message msg) {
        return msg.getExtra().getEmojiMsgDTO();
    }

    @Override
    protected void saveMsg(Message message, EmojiMsgDTO body) {
        MessageExtra msgExtra = Optional.ofNullable(message.getExtra()).orElse(new MessageExtra());
        Message update = new Message();
        update.setId(message.getId());
        update.setExtra(msgExtra);
        msgExtra.setEmojiMsgDTO(body);
        messageDao.updateById(update);

    }

    @Override
    MessageTypeEnum getMsgTypeEnum() {
        return MessageTypeEnum.EMOJI;
    }
}
