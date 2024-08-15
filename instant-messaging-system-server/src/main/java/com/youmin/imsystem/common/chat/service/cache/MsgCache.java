package com.youmin.imsystem.common.chat.service.cache;

import com.youmin.imsystem.common.chat.dao.MessageDao;
import com.youmin.imsystem.common.chat.domain.entity.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

@Component
public class MsgCache {

    @Autowired
    private MessageDao messageDao;

    @Cacheable(cacheNames = "msg",key = "'msg'+#msgId")
    public Message getMsg(Long msgId){
        return messageDao.getById(msgId);
    }
}
