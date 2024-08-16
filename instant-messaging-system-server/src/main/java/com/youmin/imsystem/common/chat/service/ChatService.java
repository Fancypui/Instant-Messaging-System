package com.youmin.imsystem.common.chat.service;

import com.youmin.imsystem.common.chat.domain.entity.Message;
import com.youmin.imsystem.common.chat.domain.vo.request.ChatMessageReq;
import com.youmin.imsystem.common.chat.domain.vo.response.ChatMessageResp;

public interface ChatService {

    /**
     * send message
     * @param request
     * @param uid
     * @return
     */
    Long sendMsg(ChatMessageReq request, Long uid);


    /**
     * get frontend display data based on provided messageRecord
     * @param messageRecord
     * @param receiveUid
     * @return
     */
    ChatMessageResp getMsgResp(Message messageRecord, Long receiveUid);

    ChatMessageResp getMsgResp(Long msgId, Long receiverUid);


}
