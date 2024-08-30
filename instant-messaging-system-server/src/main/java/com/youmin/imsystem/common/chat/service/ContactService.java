package com.youmin.imsystem.common.chat.service;

import com.youmin.imsystem.common.chat.domain.entity.Message;
import com.youmin.imsystem.common.chat.domain.vo.response.MsgReadInfoResp;

import java.util.List;
import java.util.Map;

public interface ContactService {

    Map<Long, MsgReadInfoResp> getMsgReadInfo(List<Message> messages);
}
