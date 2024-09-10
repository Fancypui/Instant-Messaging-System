package com.youmin.imsystem.common.common.event.listener;


import com.youmin.imsystem.common.chat.domain.entity.GroupMember;
import com.youmin.imsystem.common.chat.domain.entity.RoomGroup;
import com.youmin.imsystem.common.chat.domain.vo.request.ChatMessageReq;
import com.youmin.imsystem.common.chat.service.ChatService;
import com.youmin.imsystem.common.chat.service.adapter.MemberAdapter;
import com.youmin.imsystem.common.chat.service.adapter.RoomAdapter;
import com.youmin.imsystem.common.chat.service.cache.GroupMemberCache;
import com.youmin.imsystem.common.common.event.GroupMemberAddEvent;
import com.youmin.imsystem.common.user.cache.UserInfoCache;
import com.youmin.imsystem.common.user.dao.UserDao;
import com.youmin.imsystem.common.user.domain.entity.User;
import com.youmin.imsystem.common.user.domain.vo.resp.WSMemberChange;
import com.youmin.imsystem.common.user.domain.vo.resp.WSRespBase;
import com.youmin.imsystem.common.user.service.impl.PushService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class GroupMemberAddListener {

    @Autowired
    private UserInfoCache userInfoCache;

    @Autowired
    private ChatService chatService;

    @Autowired
    private GroupMemberCache groupMemberCache;

    @Autowired
    private UserDao userDao;

    @Autowired
    private PushService pushService;

    @Async
    @TransactionalEventListener(classes = GroupMemberAddEvent.class,fallbackExecution = true)
    public void sendAddMsg(GroupMemberAddEvent groupMemberAddEvent){
        List<GroupMember> groupMembers = groupMemberAddEvent.getGroupMembers();
        RoomGroup roomGorup = groupMemberAddEvent.getRoomGorup();
        Long inviter = groupMemberAddEvent.getUid();
        User user = userInfoCache.get(inviter);
        List<Long> uidList = groupMembers.stream().map(GroupMember::getUid).collect(Collectors.toList());
        ChatMessageReq chatMessageReq = RoomAdapter.buildGroupAddMessage(roomGorup, user, userInfoCache.getBatch(uidList));
        chatService.sendMsg(chatMessageReq,user.getId());
    }

    @Async
    @TransactionalEventListener(classes = GroupMemberAddEvent.class,fallbackExecution = true)
    public void sendChangePush(GroupMemberAddEvent groupMemberAddEvent){
        List<GroupMember> memberList = groupMemberAddEvent.getGroupMembers();
        RoomGroup roomGroup = groupMemberAddEvent.getRoomGorup();

        List<Long> memberUidList = groupMemberCache.getMemberList(roomGroup.getRoomId());
        List<Long> uidList = memberList.stream().map(GroupMember::getUid).collect(Collectors.toList());

        List<User> users = userDao.listByIds(uidList);
        users.forEach(user->{
            WSRespBase<WSMemberChange> wsRespBase = MemberAdapter.buildMemberAddWS(roomGroup.getRoomId(), user);
            pushService.pushService(wsRespBase,memberUidList);
        });
        groupMemberCache.evictMemberUidList(roomGroup.getRoomId());

    }


}
