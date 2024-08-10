package com.youmin.imsystem.common.chat.service.impl;

import com.youmin.imsystem.common.chat.dao.GroupMemberDao;
import com.youmin.imsystem.common.chat.dao.RoomFriendDao;
import com.youmin.imsystem.common.chat.dao.RoomGroupDao;
import com.youmin.imsystem.common.chat.domain.entity.GroupMember;
import com.youmin.imsystem.common.chat.domain.entity.Room;
import com.youmin.imsystem.common.chat.domain.entity.RoomFriend;
import com.youmin.imsystem.common.chat.domain.entity.RoomGroup;
import com.youmin.imsystem.common.chat.domain.vo.request.ChatMessageReq;
import com.youmin.imsystem.common.chat.service.ChatService;
import com.youmin.imsystem.common.chat.service.cache.RoomCache;
import com.youmin.imsystem.common.chat.service.cache.RoomGroupCache;
import com.youmin.imsystem.common.chat.service.strategy.msg.AbstractMsgHandler;
import com.youmin.imsystem.common.chat.service.strategy.msg.MsgHandlerFactory;
import com.youmin.imsystem.common.common.domain.enums.NormalOrNotEnum;
import com.youmin.imsystem.common.common.event.MessageSendEvent;
import com.youmin.imsystem.common.common.utils.AssertUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ChatServiceImpl implements ChatService {

    @Autowired
    private RoomCache roomCache;

    @Autowired
    private RoomGroupCache roomGroupCache;




    @Autowired
    private RoomFriendDao roomFriendDao;
    @Autowired
    private RoomGroupDao roomGroupDao;

    @Autowired
    private GroupMemberDao groupMemberDao;

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    @Override
    @Transactional
    public Long sendMsg(ChatMessageReq request, Long uid) {
        check(request,uid);
        AbstractMsgHandler<?> msgHandler = MsgHandlerFactory.getStrategyOrNull(request.getMsgType());
        Long msgId =  msgHandler.checkAndSaveMsg(request, uid);
        //publish message sent event
        applicationEventPublisher.publishEvent(new MessageSendEvent(this,msgId));
        return msgId;
    }

    private void check(ChatMessageReq req,Long uid){
        Room room = roomCache.get(req.getRoomId());
        if(room.isHot()){//big group skip validation
            return;
        }
        if(room.isRoomFriend()){
            RoomFriend roomFriend = roomFriendDao.getById(room.getId());
            //having concurrency issue where friend delete current user and current user send msg at the same time
            //this is not an issue if current user send a message to a deleted friend
            //condition will be made when pulling history chat
            AssertUtils.equal(NormalOrNotEnum.NORMAL.getStatus(),roomFriend.getStatus(),"You has been blocked by your friend");
            AssertUtils.isTrue(uid.equals(roomFriend.getUid1())||uid.equals(roomFriend.getUid2()),"You has been blocked by your friend");
        }
        if(room.isRoomGroup()){
            RoomGroup roomGroup = roomGroupCache.get(room.getId());
            GroupMember groupMember = groupMemberDao.getMember(roomGroup.getRoomId(), uid);
            AssertUtils.isNotEmpty(groupMember,"You have been remove from the group");
        }

    }
}

