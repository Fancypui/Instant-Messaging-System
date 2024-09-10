package com.youmin.imsystem.common.chat.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Pair;
import com.youmin.imsystem.common.chat.dao.*;
import com.youmin.imsystem.common.chat.domain.entity.*;
import com.youmin.imsystem.common.chat.domain.enums.MessageMarkActTypeEnum;
import com.youmin.imsystem.common.chat.domain.enums.MessageTypeEnum;
import com.youmin.imsystem.common.chat.domain.vo.request.*;
import com.youmin.imsystem.common.chat.domain.vo.response.*;
import com.youmin.imsystem.common.chat.service.ChatService;
import com.youmin.imsystem.common.chat.service.ContactService;
import com.youmin.imsystem.common.chat.service.adapter.ChatAdapter;
import com.youmin.imsystem.common.chat.service.adapter.MemberAdapter;
import com.youmin.imsystem.common.chat.service.adapter.MessageAdapter;
import com.youmin.imsystem.common.chat.service.adapter.RoomAdapter;
import com.youmin.imsystem.common.chat.service.cache.RoomCache;
import com.youmin.imsystem.common.chat.service.cache.RoomGroupCache;
import com.youmin.imsystem.common.chat.service.helper.ChatMemberHelper;
import com.youmin.imsystem.common.chat.service.strategy.mark.AbstractMessageMark;
import com.youmin.imsystem.common.chat.service.strategy.mark.MessageMarkStrategyFactory;
import com.youmin.imsystem.common.chat.service.strategy.msg.AbstractMsgHandler;
import com.youmin.imsystem.common.chat.service.strategy.msg.MsgHandlerFactory;
import com.youmin.imsystem.common.chat.service.strategy.msg.RecallMsgHandler;
import com.youmin.imsystem.common.common.annotation.RedissonLock;
import com.youmin.imsystem.common.common.domain.enums.NormalOrNotEnum;
import com.youmin.imsystem.common.common.domain.vo.req.CursorBaseReq;
import com.youmin.imsystem.common.common.domain.vo.resp.CursorPageBaseResp;
import com.youmin.imsystem.common.common.event.MessageSendEvent;
import com.youmin.imsystem.common.common.exception.BusinessException;
import com.youmin.imsystem.common.common.utils.AssertUtils;
import com.youmin.imsystem.common.user.cache.UserCache;
import com.youmin.imsystem.common.user.dao.UserDao;
import com.youmin.imsystem.common.user.domain.entity.User;
import com.youmin.imsystem.common.user.enums.ChatActiveStatusEnum;
import com.youmin.imsystem.common.user.enums.RoleEnum;
import com.youmin.imsystem.common.user.service.IRoleService;
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
    private ContactService contactService;

    @Autowired
    private UserDao userDao;

    @Autowired
    private UserCache userCache;



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
            GroupMember groupMember = groupMemberDao.getMember(roomGroup.getId(), uid);
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

    @Override
    @RedissonLock(key = "#uid")
    public void setMessageMark(MessageMarkReq request, Long uid) {
        AbstractMessageMark strategy = MessageMarkStrategyFactory.getStrategyOrNull(request.getMarkType());
        switch (MessageMarkActTypeEnum.of(request.getActType())){
            case MARK:
                strategy.mark(uid, request.getMsgId());
                return;
            case UN_MARK:
                strategy.unmark(uid, request.getMsgId());
                return;
        }
    }

    @Override
    @RedissonLock(key = "#uid")
    public void  msgRead(ChatMessageReadReq request, Long uid) {
        Contact contact = contactDao.getByRoomIdAndUId(request.getRoomId(), uid);
        if(Objects.nonNull(contact)){
            Contact update = new Contact();
            update.setId(contact.getId());
            update.setReadTime(new Date());
            contactDao.updateById(update);
        }else {
            Contact insert = new Contact();
            insert.setUid(uid);
            insert.setRoomId(request.getRoomId());
            insert.setReadTime(new Date());
            contactDao.save(insert);
        }
    }

    @Override
    public Collection<MsgReadInfoResp> getMsgReadInfo(ChatMessageReadInfoReq request, Long uid) {
        List<Message> messages = messageDao.listByIds(request.getMsgIds());
        messages.forEach(message -> {
                    AssertUtils.equal(uid,message.getFromUid(),"You are not authorised to view");
        });
        return contactService.getMsgReadInfo(messages).values();

    }

    @Override
    public CursorPageBaseResp<ChatMessageReadResp> getReadPage(ChatMessageReadPageReq request, Long uid) {
        Message message = messageDao.getById(request.getMsgId());
        AssertUtils.isNotEmpty(message,"Invalid message");
        AssertUtils.equal(message.getFromUid(),uid,"Cannot view");
        CursorPageBaseResp<Contact> page;
        if(request.getSearchType()==1){
            page = contactDao.getReadPage(request,message);
        }else{
            page = contactDao.getUnReadPage(request,message);
        }
        return CursorPageBaseResp.init(page, RoomAdapter.buildReadResp(page.getList()));
    }

    @Override
    public CursorPageBaseResp<ChatMemberResp> getMemberPage(MemberReq req, List<Long> memberUidList) {
        Pair<ChatActiveStatusEnum, String> cursorPair = ChatMemberHelper.getCursorPair(req.getCursor());
        ChatActiveStatusEnum activeStatusEnum = cursorPair.getKey();
        String timeCursor = cursorPair.getValue();
        List<ChatMemberResp> result = new ArrayList<>();//final list response
        Boolean isLast = Boolean.FALSE;
        if(activeStatusEnum == ChatActiveStatusEnum.ONLINE){//online list
            CursorPageBaseResp<User> userPage =
                    userDao.getUserPage(memberUidList, new CursorBaseReq(req.getPageSize(), timeCursor), activeStatusEnum);
            result.addAll(MemberAdapter.buildChatMemberResp(userPage.getList()));
            if(userPage.getIsLast()){//load extra record from offline user if online page is last page
                activeStatusEnum = ChatActiveStatusEnum.OFFLINE;
                Integer leftSize = req.getPageSize() - userPage.getList().size();
                userPage = userDao.getUserPage(memberUidList, new CursorBaseReq(leftSize,null),activeStatusEnum);
                result.addAll(MemberAdapter.buildChatMemberResp(userPage.getList()));
            }
            timeCursor = userPage.getCursor();
            isLast = userPage.getIsLast();
        }else if(activeStatusEnum == ChatActiveStatusEnum.OFFLINE){
            CursorPageBaseResp<User> userPage =
                    userDao.getUserPage(memberUidList, new CursorBaseReq(req.getPageSize(), timeCursor), activeStatusEnum);
            result.addAll(MemberAdapter.buildChatMemberResp(userPage.getList()));
            timeCursor = userPage.getCursor();
            isLast = userPage.getIsLast();
        }
        //get group member role id
        List<Long> uidList = result.stream().map(ChatMemberResp::getUid).collect(Collectors.toList());
        RoomGroup roomGroup = roomGroupCache.get(req.getRoomId());
        Map<Long, Integer> memberMapRole = groupMemberDao.getMemberMapRole(uidList, roomGroup.getId());
        result.forEach(member->member.setRoleId(memberMapRole.get(member.getUid())));
        return new CursorPageBaseResp<>(ChatMemberHelper.generateCursor(timeCursor,activeStatusEnum),isLast,result);
    }

    @Override
    public ChatMemberStatisticResp getMemberStatisticResp() {
        Long onlineNum = userCache.getOnlineNum();
        ChatMemberStatisticResp resp = new ChatMemberStatisticResp();
        resp.setOnlineNum(onlineNum);
        return resp;
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

