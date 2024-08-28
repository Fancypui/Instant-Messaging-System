package com.youmin.imsystem.common.chat.service.strategy.mark;

import com.youmin.imsystem.common.chat.dao.MessageDao;
import com.youmin.imsystem.common.chat.dao.MessageMarkDao;
import com.youmin.imsystem.common.chat.domain.dto.ChatMessageMarkDTO;
import com.youmin.imsystem.common.chat.domain.entity.MessageMark;
import com.youmin.imsystem.common.chat.domain.enums.MessageMarkActTypeEnum;
import com.youmin.imsystem.common.chat.domain.enums.MessageMarkTypeEnum;
import com.youmin.imsystem.common.common.domain.enums.NormalOrNotEnum;
import com.youmin.imsystem.common.common.domain.enums.YesOrNoEnum;
import com.youmin.imsystem.common.common.event.MessageMarkEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.Objects;
import java.util.Optional;

public abstract class AbstractMessageMark {

    @Autowired
    private MessageMarkDao messageMarkDao;

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    @PostConstruct
    public void init(){
        MessageMarkStrategyFactory.register(getMarkType().getType(),this);
    }

    public abstract MessageMarkTypeEnum getMarkType();

    @Transactional
    public void mark(Long uid, Long msgId){
        doMark(uid,msgId);
    }

    @Transactional
    public void unmark(Long uid, Long msgId){
        doUnMark(uid,msgId);
    }

    protected void doMark(Long uid, Long msgId){
        exec(uid,msgId,MessageMarkActTypeEnum.MARK);
    }
    protected void doUnMark(Long uid, Long msgId){
        exec(uid,msgId,MessageMarkActTypeEnum.UN_MARK);
    }

    protected void exec(Long uid, Long msgId,MessageMarkActTypeEnum actTypeEnum){
        Integer markType = getMarkType().getType();
        Integer actType = actTypeEnum.getType();
        MessageMark oldMark = messageMarkDao.get(uid, msgId, markType);
        if(Objects.isNull(oldMark)&& actType==MessageMarkActTypeEnum.UN_MARK.getType()){
            //if it is a cancel action, there will have a record in database
            //if it does not exist, return
            return;
        }
        //save or update a record
        MessageMark saveOrUpdate = MessageMark.builder()
                .id(Optional.ofNullable(oldMark).map(MessageMark::getId).orElse(null))
                .msgId(msgId)
                .uid(uid)
                .type(markType)
                .status(transformActType(actTypeEnum))
                .build();
        boolean modify = messageMarkDao.saveOrUpdate(saveOrUpdate);
        if(modify){
            ChatMessageMarkDTO dto = new ChatMessageMarkDTO(uid, msgId, markType, actType);
            applicationEventPublisher.publishEvent(new MessageMarkEvent(this,dto));
        }


    }
    private Integer transformActType(MessageMarkActTypeEnum actTypeEnum){
        switch (actTypeEnum){
            case MARK:
                return NormalOrNotEnum.NORMAL.getStatus();
            case UN_MARK:
                return NormalOrNotEnum.NOT_NORMAL.getStatus();
            default:
                return -1;
        }
    }
}
