package com.youmin.imsystem.common.chat.service.adapter;

import com.youmin.imsystem.common.chat.domain.entity.Message;
import com.youmin.imsystem.common.chat.domain.enums.MessageStatusEnum;
import com.youmin.imsystem.common.chat.domain.vo.request.ChatMessageReq;

public class MessageAdapter {

    public static Message buildMessageSave(ChatMessageReq request, Long uid){
        Message message = new Message();
        message.setRoomId(request.getRoomId());
        message.setFromUid(uid);
        message.setType(request.getMsgType());
        message.setStatus(MessageStatusEnum.NORMAL.getStatus());
        return message;
    }
}
