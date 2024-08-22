package com.youmin.imsystem.common.chat.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import com.youmin.imsystem.common.chat.dao.*;
import com.youmin.imsystem.common.chat.domain.entity.*;
import com.youmin.imsystem.common.chat.domain.enums.MessageTypeEnum;
import com.youmin.imsystem.common.chat.domain.vo.request.ChatMessagePageRequest;
import com.youmin.imsystem.common.chat.domain.vo.request.ChatMessageRecallReq;
import com.youmin.imsystem.common.chat.domain.vo.request.ChatMessageReq;
import com.youmin.imsystem.common.chat.domain.vo.response.ChatMessageResp;
import com.youmin.imsystem.common.chat.service.ChatService;
import com.youmin.imsystem.common.chat.service.adapter.MessageAdapter;
import com.youmin.imsystem.common.chat.service.cache.RoomCache;
import com.youmin.imsystem.common.chat.service.cache.RoomGroupCache;
import com.youmin.imsystem.common.chat.service.strategy.msg.AbstractMsgHandler;
import com.youmin.imsystem.common.chat.service.strategy.msg.MsgHandlerFactory;
import com.youmin.imsystem.common.chat.service.strategy.msg.RecallMsgHandler;
import com.youmin.imsystem.common.common.domain.enums.NormalOrNotEnum;
import com.youmin.imsystem.common.common.domain.vo.req.CursorBaseReq;
import com.youmin.imsystem.common.common.domain.vo.resp.CursorPageBaseResp;
import com.youmin.imsystem.common.common.event.MessageSendEvent;
import com.youmin.imsystem.common.common.exception.BusinessException;
import com.youmin.imsystem.common.common.utils.AssertUtils;
import com.youmin.imsystem.common.user.enums.RoleEnum;
import com.youmin.imsystem.common.user.service.IRoleService;
import javafx.print.Collation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ChatServiceImpl implements ChatService {

    @Autowired
    private RoomCache roomCache;

    @Autowired
    private RoomGroupCache roomGroupCache;
    @Autowired
    private MessageDao messageDao;


    @Autowired
    private MessageMarkDao messageMarkDao;

    @Autowired
    private RoomFriendDao roomFriendDao;
    @Autowired
    private RoomGroupDao roomGroupDao;

    @Autowired
    private GroupMemberDao groupMemberDao;

    @Autowired
    private ContactDao contactDao;

    @Autowired
    private IRoleService roleService;

    @Autowired
    private RecallMsgHandler recallMsgHandler;



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

    @Override
    public ChatMessageResp getMsgResp(Message message, Long receiveUid) {
        return CollectionUtil.getFirst(getBatchMsgResp(Collections.singletonList(message),receiveUid));
    }

    @Override
    public ChatMessageResp getMsgResp(Long msgId, Long receiverUid) {
        Message message = messageDao.getById(msgId);
        return getMsgResp(message,receiverUid);
    }

    @Override
    public CursorPageBaseResp<ChatMessageResp> getMsgPage(ChatMessagePageRequest request, Long receiverId) {
        //lastMsgId is used to avoid user extracting new msg from table after user has been remove
        Long lastMsgId = getLastMsgId(request.getRoomId(), receiverId);
        CursorPageBaseResp<Message> msgPage = messageDao.getMsgPage(request, receiverId, lastMsgId);
        if(CollectionUtil.isEmpty(msgPage.getList())){
            return CursorPageBaseResp.empty();
        }
        return CursorPageBaseResp.init(msgPage,getBatchMsgResp(msgPage.getList(),receiverId));
    }

    @Override
    public void recallMsg(ChatMessageRecallReq request, Long uid) {
        Message message = messageDao.getById(request.getMsgId());
        //validate if message can be recall
        checkRecall(message,uid);
        //execute message recall
        recallMsgHandler.recall(uid,message);
    }


    private void checkRecall(Message message, Long uid){
        AssertUtils.isNotEmpty(message,"Message is not found in database");
        AssertUtils.notEqual(message.getType(), MessageTypeEnum.RECALL.getMsgType(),"Message has been recalled");
        //chat manager has the power to recall all message
        boolean hasPower = roleService.hasPower(uid, RoleEnum.CHAT_MANAGER);
        if(hasPower){
            return;
        }
        AssertUtils.equal(message.getFromUid(),uid, "Message cannot be recalled as it is not sent by you");
        //after 2 minutes cannot be recall
        if(DateUtil.between(message.getCreateTime(),new Date(), DateUnit.MINUTE)>2){
            throw new BusinessException("Message cannot be recalled anymore as its sending time has exceeded 2 minutes");
        }
    }

    private Long getLastMsgId(Long roomId, Long receiverId){
        Room room = roomCache.get(roomId);
        AssertUtils.isNotEmpty(room,"Invalida room id");
        if(room.isHot()){
            return null;
        }
        //todo test if receiver is not a member of the room, what will be returned by mybatis
        Contact contact = contactDao.getLastMsgId(roomId,receiverId);
        return contact.getLastMsgId();
    }

    public List<ChatMessageResp> getBatchMsgResp(List<Message> messages, Long receiveUid) {
        if(CollectionUtil.isEmpty(messages)){
            return new ArrayList<>();
        }
        //query messageMarks
        List<MessageMark> messageMarks = messageMarkDao.getValidBatchMessageMark(messages.stream()
                .map(Message::getId).collect(Collectors.toList()));
        return MessageAdapter.buildMsgResp(messages,messageMarks,receiveUid);

    }
}

