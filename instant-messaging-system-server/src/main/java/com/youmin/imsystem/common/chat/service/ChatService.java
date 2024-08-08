package com.youmin.imsystem.common.chat.service;

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
}
