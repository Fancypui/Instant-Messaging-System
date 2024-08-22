package com.youmin.imsystem.common.chat.service.cache;

import com.youmin.imsystem.common.chat.dao.MessageDao;
import com.youmin.imsystem.common.chat.domain.entity.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;

@Component
public class MsgCache {

    @Autowired
    private MessageDao messageDao;

    @Cacheable(cacheNames = "msg",key = "'msg'+#msgId")
    public Message getMsg(Long msgId){
        return messageDao.getById(msgId);
    }

    @CacheEvict(cacheNames = "msg", key =  "'msg'+#msgId")
    public Message evictMsg(Long msgId){
        return null;
    }
}
