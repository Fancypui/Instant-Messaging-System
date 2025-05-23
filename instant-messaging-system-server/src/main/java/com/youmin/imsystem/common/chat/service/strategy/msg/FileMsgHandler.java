package com.youmin.imsystem.common.chat.service.strategy.msg;

import com.youmin.imsystem.common.chat.dao.MessageDao;
import com.youmin.imsystem.common.chat.domain.entity.Message;
import com.youmin.imsystem.common.chat.domain.entity.msg.FileMsgDTO;
import com.youmin.imsystem.common.chat.domain.entity.msg.MessageExtra;
import com.youmin.imsystem.common.chat.domain.enums.MessageTypeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class FileMsgHandler extends AbstractMsgHandler<FileMsgDTO>{
    @Autowired
    private MessageDao messageDao;

    @Override
    public Object showMsg(Message msg) {
        return msg.getExtra().getFileMsgDTO();
    }

    @Override
    public Object showReplyMsg(Message msg) {
        return "File"+msg.getExtra().getFileMsgDTO().getFileName();
    }

    @Override
    protected void saveMsg(Message message, FileMsgDTO body) {
        MessageExtra msgExtra = Optional.ofNullable(message.getExtra()).orElse(new MessageExtra());
        Message update = new Message();
        update.setId(message.getId());
        update.setExtra(msgExtra);
        msgExtra.setFileMsgDTO(body);
        messageDao.updateById(update);
    }

    @Override
    MessageTypeEnum getMsgTypeEnum() {
        return MessageTypeEnum.FILE;
    }

    @Override
    public String showContactMsg(Message msg) {
        return "[File]"+msg.getExtra().getFileMsgDTO().getFileName();
    }
}
