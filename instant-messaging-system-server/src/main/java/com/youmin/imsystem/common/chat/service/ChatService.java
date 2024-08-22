package com.youmin.imsystem.common.chat.service;

import com.youmin.imsystem.common.chat.domain.entity.Message;
import com.youmin.imsystem.common.chat.domain.vo.request.ChatMessagePageRequest;
import com.youmin.imsystem.common.chat.domain.vo.request.ChatMessageRecallReq;
import com.youmin.imsystem.common.chat.domain.vo.request.ChatMessageReq;
import com.youmin.imsystem.common.chat.domain.vo.response.ChatMessageResp;
import com.youmin.imsystem.common.common.domain.vo.req.CursorBaseReq;
import com.youmin.imsystem.common.common.domain.vo.resp.CursorPageBaseResp;

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

    /**
     * get msg page using cursor
     * @param request
     * @param receiverId
     * @return
     */
    CursorPageBaseResp<ChatMessageResp> getMsgPage(ChatMessagePageRequest request, Long receiverId);

    /**
     * recall msg
     * @param request
     * @param uid
     */
    void recallMsg(ChatMessageRecallReq request, Long uid);
}
