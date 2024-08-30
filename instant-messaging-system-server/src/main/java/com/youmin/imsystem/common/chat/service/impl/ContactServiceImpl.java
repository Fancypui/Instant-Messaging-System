package com.youmin.imsystem.common.chat.service.impl;

import com.youmin.imsystem.common.chat.dao.ContactDao;
import com.youmin.imsystem.common.chat.dao.MessageDao;
import com.youmin.imsystem.common.chat.domain.entity.Message;
import com.youmin.imsystem.common.chat.domain.vo.response.MsgReadInfoResp;
import com.youmin.imsystem.common.chat.service.ContactService;
import com.youmin.imsystem.common.common.utils.AssertUtils;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ContactServiceImpl implements ContactService {
    @Autowired
    private ContactDao contactDao;
    @Override
    public Map<Long, MsgReadInfoResp> getMsgReadInfo(List<Message> messages) {
        Map<Long, List<Message>> groupMessageMap = messages.stream().collect(Collectors.groupingBy(Message::getRoomId));
        AssertUtils.equal(groupMessageMap.size(),1,"Cannot read message info from more than one room");
        Long roomId = groupMessageMap.keySet().iterator().next();
        Integer totalCount = contactDao.getTotalCount(roomId);
        return messages.stream().map(message -> {
            MsgReadInfoResp msgReadInfoResp = new MsgReadInfoResp();
            msgReadInfoResp.setMsgId(message.getId());
            Integer readCount = contactDao.getReadCount(message);
            msgReadInfoResp.setReadCount(readCount);
            msgReadInfoResp.setUnReadCount(totalCount-readCount-1);
            return msgReadInfoResp;
        }).collect(Collectors.toMap(MsgReadInfoResp::getMsgId, Function.identity()));
    }
}
