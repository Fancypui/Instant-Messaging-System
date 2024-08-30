package com.youmin.imsystem.common.chat.service.strategy.msg;

import com.youmin.imsystem.common.chat.dao.MessageDao;
import com.youmin.imsystem.common.chat.domain.entity.Message;
import com.youmin.imsystem.common.chat.domain.entity.msg.ImgMsgDTO;
import com.youmin.imsystem.common.chat.domain.entity.msg.MessageExtra;
import com.youmin.imsystem.common.chat.domain.enums.MessageTypeEnum;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class ImgMsgHandler extends AbstractMsgHandler<ImgMsgDTO> {
    private MessageDao messageDao;

    @Override
    public Object showMsg(Message msg) {
        return msg.getExtra().getImgMsgDTO();
    }

    @Override
    public Object showReplyMsg(Message msg) {
        return "Image";
    }

    @Override
    protected void saveMsg(Message message, ImgMsgDTO body) {
        MessageExtra msgExtra = Optional.ofNullable(message.getExtra()).orElse(new MessageExtra());
        Message update = new Message();
        update.setExtra(msgExtra);
        update.setId(message.getId());
        msgExtra.setImgMsgDTO(body);
        messageDao.updateById(update);
    }

    @Override
    MessageTypeEnum getMsgTypeEnum() {
        return MessageTypeEnum.IMG;
    }

    @Override
    public String showContactMsg(Message msg) {
        return "[Image]";
    }
}
