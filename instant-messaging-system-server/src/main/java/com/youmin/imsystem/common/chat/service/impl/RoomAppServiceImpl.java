package com.youmin.imsystem.common.chat.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Pair;
import com.youmin.imsystem.common.chat.dao.ContactDao;
import com.youmin.imsystem.common.chat.dao.GroupMemberDao;
import com.youmin.imsystem.common.chat.dao.MessageDao;
import com.youmin.imsystem.common.chat.domain.dto.RoomBaseInfo;
import com.youmin.imsystem.common.chat.domain.entity.*;
import com.youmin.imsystem.common.chat.domain.enums.GroupRoleAppEnum;
import com.youmin.imsystem.common.chat.domain.enums.GroupRoleEnum;
import com.youmin.imsystem.common.chat.domain.enums.RoomTypeEnums;
import com.youmin.imsystem.common.chat.domain.vo.request.*;
import com.youmin.imsystem.common.chat.domain.vo.response.ChatMemberListResp;
import com.youmin.imsystem.common.chat.domain.vo.response.ChatMemberResp;
import com.youmin.imsystem.common.chat.domain.vo.response.ChatRoomResp;
import com.youmin.imsystem.common.chat.domain.vo.response.MemberResp;
import com.youmin.imsystem.common.chat.service.ChatService;
import com.youmin.imsystem.common.chat.service.IRoomService;
import com.youmin.imsystem.common.chat.service.RoomAppService;
import com.youmin.imsystem.common.chat.service.adapter.ChatAdapter;
import com.youmin.imsystem.common.chat.service.adapter.MemberAdapter;
import com.youmin.imsystem.common.chat.service.adapter.RoomAdapter;
import com.youmin.imsystem.common.chat.service.cache.*;
import com.youmin.imsystem.common.chat.service.strategy.msg.AbstractMsgHandler;
import com.youmin.imsystem.common.chat.service.strategy.msg.MsgHandlerFactory;
import com.youmin.imsystem.common.common.annotation.RedissonLock;
import com.youmin.imsystem.common.common.domain.vo.req.CursorBaseReq;
import com.youmin.imsystem.common.common.domain.vo.req.IdReqVO;
import com.youmin.imsystem.common.common.domain.vo.resp.CursorPageBaseResp;
import com.youmin.imsystem.common.common.event.GroupMemberAddEvent;
import com.youmin.imsystem.common.common.exception.GroupErrorEnum;
import com.youmin.imsystem.common.common.utils.AssertUtils;
import com.youmin.imsystem.common.user.cache.UserCache;
import com.youmin.imsystem.common.user.cache.UserInfoCache;
import com.youmin.imsystem.common.user.dao.UserDao;
import com.youmin.imsystem.common.user.domain.entity.User;
import com.youmin.imsystem.common.user.domain.vo.resp.WSMemberChange;
import com.youmin.imsystem.common.user.domain.vo.resp.WSRespBase;
import com.youmin.imsystem.common.user.enums.RoleEnum;
import com.youmin.imsystem.common.user.service.IRoleService;
import com.youmin.imsystem.common.user.service.impl.PushService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class RoomAppServiceImpl implements RoomAppService {

    @Autowired
    private HotRoomCache hotRoomCache;

    @Autowired
    private MessageDao messageDao;

    @Autowired
    private RoomCache roomCache;

    @Autowired
    private ContactDao contactDao;
    
    @Autowired
    private RoomGroupCache roomGroupCache;

    @Autowired
    private RoomFriendCache roomFriendCache;

    @Autowired
    private UserInfoCache userInfoCache;

    @Autowired
    private IRoomService roomService;

    @Autowired
    private GroupMemberCache groupMemberCache;

    @Autowired
    private ChatService chatService;

    @Autowired
    private UserCache userCache;

    @Autowired
    private UserDao userDao;
    @Autowired
    private GroupMemberDao groupMemberDao;

    @Autowired
    private IRoleService iRoleService;

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    @Autowired
    private PushService pushService;


    @Override
    public CursorPageBaseResp<ChatRoomResp> getRoomPage(CursorBaseReq request, Long uid) {
        //extract all records that need to be display
        CursorPageBaseResp<Long> page;
        //Uid is null means user hasnt login, so user only can see hot flag room
        if(Objects.nonNull(uid)){
            Double hotEnd = getCursorOrNull(request.getCursor());
            Double hotStart = null;
            //base user contact
            CursorPageBaseResp<Contact> contactPage = contactDao.getContactPage(request, uid);
            List<Long> baseRoomIds = contactPage.getList().stream().map(Contact::getRoomId).collect(Collectors.toList());
            if(!contactPage.getIsLast()){
                hotStart = getCursorOrNull(contactPage.getCursor());
            }
            //hot room
            Set<ZSetOperations.TypedTuple<String>> roomRange = hotRoomCache.getRoomRange(hotStart, hotEnd);
            List<Long> hotRoomIds = roomRange.stream().map(ZSetOperations.TypedTuple::getValue).filter(Objects::nonNull)
                    .map(Long::parseLong).collect(Collectors.toList());
            baseRoomIds.addAll(hotRoomIds);//merge hot room and normal room
            page=CursorPageBaseResp.init(contactPage,baseRoomIds);
        }else {
            CursorPageBaseResp<Pair<Long, Double>> roomCursorPage = hotRoomCache.getRoomCursorPage(request);
            List<Long> roomIds = roomCursorPage.getList().stream().map(Pair::getKey).collect(Collectors.toList());
            page = CursorPageBaseResp.init(roomCursorPage, roomIds);
        }
        List<ChatRoomResp> resp = buildContactResp(page.getList(),uid);
        return CursorPageBaseResp.init(page,resp);
    }

    @Override
    public ChatRoomResp getContactDetail(IdReqVO request, Long uid) {
        Room room = roomCache.get(request.getId());
        AssertUtils.isNotEmpty(room,"Invalid Room");
        return buildContactResp(Collections.singletonList(room.getId()),uid).get(0);
    }

    @Override
    public ChatRoomResp getContactDetailByFriend(ContactFriendReq request, Long uid) {
        RoomFriend friendRoom = roomService.getFriendRoom(uid, request.getUid());
        AssertUtils.isNotEmpty(friendRoom,"He/She is not your friend");
        return buildContactResp(Collections.singletonList(friendRoom.getRoomId()),uid).get(0);
    }

    @Override
    public CursorPageBaseResp<ChatMemberResp> getMemberPage(MemberReq req) {
        Room room = roomCache.get(req.getRoomId());
        AssertUtils.isNotEmpty(room, "Invalid room");
        List<Long> memberUidList;
        if(room.isHot()){//if it is hot room. perform cursor query on all user
            memberUidList = null;
        }else {//only display member in the room group
            RoomGroup roomGroup = roomGroupCache.get(req.getRoomId());
            memberUidList = groupMemberCache.getMemberList(roomGroup.getId());
        }
        return chatService.getMemberPage(req,memberUidList);

    }

    @Override
    public MemberResp getGroupDetail(IdReqVO request, Long uid) {
        RoomGroup roomGroup = roomGroupCache.get(request.getId());
        Room room = roomCache.get(request.getId());
        AssertUtils.isNotEmpty(room,"Invalid Room");
        Long onlineCount;
        if(room.isHot()){//get online member count from redis if it is hot room
            onlineCount = userCache.getOnlineNum();
        }else{
            List<Long> memberList = groupMemberCache.getMemberList(room.getId());
            onlineCount = userDao.getOnlineCount(memberList).longValue();
        }
        GroupRoleAppEnum groupRole = getGroupRole(room, roomGroup, uid);
        return MemberResp.builder()
                .roomId(room.getId())
                .role(groupRole.getType())
                .avatar(roomGroup.getAvatar())
                .groupName(roomGroup.getName())
                .onlineNum(onlineCount)
                .build();
    }

    @Override
    public List<ChatMemberListResp> getMemberList(MemberReq request) {
        Room room = roomCache.get(request.getRoomId());
        AssertUtils.isNotEmpty(room,"Invalid room");
        if(room.isHot()){
            List<User> memberList = userDao.getMemberList();
            return MemberAdapter.buiidMemberListResp(memberList);
        }else{
            List<Long> uidList = groupMemberCache.getMemberList(room.getId());
            Map<Long, User> batch = userInfoCache.getBatch(uidList);
            return MemberAdapter.buiidMemberListResp(new ArrayList<>(batch.values()));
        }

    }

    @Override
    public Long addGroup(GroupAddReq request, Long uid) {
        RoomGroup groupRoom = roomService.createGroupRoom(uid);
        //batch insert groupMembers
        List<GroupMember> groupMembers = RoomAdapter.buildGroupMemberBatch(request.getUidList(), groupRoom.getId());
        groupMemberDao.saveBatch(groupMembers);
        applicationEventPublisher.publishEvent(new GroupMemberAddEvent(this, groupMembers,uid,groupRoom));
        return groupRoom.getRoomId();
    }

    @Override
    @RedissonLock(key = "#request.roomId")
    @Transactional
    public void addMember(MemberAddReq request, Long uid) {
        Room room = roomCache.get(request.getRoomId());
        AssertUtils.isNotEmpty(room,"Invalid room");
        AssertUtils.isFalse(room.isHot(),"Big room group no need to invite user");
        RoomGroup roomGroup = roomGroupCache.get(room.getId());
        AssertUtils.isNotEmpty(room,"Invalid room");
        GroupMember member = groupMemberDao.getMember(roomGroup.getId(), uid);
        AssertUtils.isNotEmpty(member,"you are not group member");
        List<Long> memberBatch = groupMemberDao.getMemberBatch(request.getRoomId(), request.getUidList());
        HashSet<Long> existUid = new HashSet<>(memberBatch);
        List<Long> waitAddUidList = request.getUidList().stream().filter(a -> !existUid.contains(a)).distinct().collect(Collectors.toList());
        if(CollectionUtil.isEmpty(waitAddUidList)){
            return;
        }
        List<GroupMember> groupMembers = MemberAdapter.buildMemberAdd(roomGroup.getId(), waitAddUidList);
        groupMemberDao.saveBatch(groupMembers);
        applicationEventPublisher.publishEvent(new GroupMemberAddEvent(this,groupMembers,uid,roomGroup));
    }

    @Override
    public void delMember(MemberDelReq request, Long uid) {
        Room room = roomCache.get(request.getRoomId());
        AssertUtils.isNotEmpty(room,"Invalid Room");
        RoomGroup roomGroup = roomGroupCache.get(room.getId());
        AssertUtils.isNotEmpty(roomGroup, "Invalid Room");
        GroupMember self = groupMemberDao.getMember(roomGroup.getId(), uid);
        AssertUtils.isNotEmpty(self, GroupErrorEnum.USER_NOT_IN_GROUP);
        //1. check if request the remove user is admin or group owner, admin can only be removed by group owner, group owner cannot exit
        Long removedUid = request.getUid();
        AssertUtils.isFalse(groupMemberDao.isLord(roomGroup.getId(),removedUid),GroupErrorEnum.NOT_ALLOWED_FOR_REMOVE);
        //check if removeUid is admin
        if(groupMemberDao.isManager(roomGroup.getId(),removedUid)){
            boolean isLord = groupMemberDao.isLord(roomGroup.getId(), uid);
            AssertUtils.isTrue(isLord,GroupErrorEnum.NOT_ALLOWED_FOR_REMOVE);

        }
        AssertUtils.isTrue(hasPower(self),GroupErrorEnum.NOT_ALLOWED_FOR_REMOVE);
        GroupMember member = groupMemberDao.getMember(roomGroup.getId(), removedUid);
        AssertUtils.isNotEmpty(member,"User ady been removed");
        groupMemberDao.removeById(member);
        List<Long> memberList = groupMemberCache.getMemberList(roomGroup.getRoomId());
        WSRespBase<WSMemberChange> ws = MemberAdapter.buildMemberRemoveWS(roomGroup.getRoomId(), member.getUid());
        pushService.pushService(ws,memberList);
        groupMemberCache.evictMemberUidList(room.getId());
    }

    private boolean hasPower(GroupMember self) {
        return Objects.equals(self.getRole(),GroupRoleAppEnum.LEADER)
                || Objects.equals(self.getRole(), GroupRoleEnum.MANAGER.getType())
                || iRoleService.hasPower(self.getUid(), RoleEnum.ADMIN);
    }

    private GroupRoleAppEnum getGroupRole(Room room, RoomGroup roomGroup, Long uid) {
        GroupMember member = Objects.isNull(uid)?null:groupMemberDao.getMember(roomGroup.getId(), uid);
        if(Objects.nonNull(member)){
            return GroupRoleAppEnum.of(member.getRole());
        }else if(room.isHot()){
            return GroupRoleAppEnum.MEMBER;
        }else{
            return GroupRoleAppEnum.REMOVE;
        }
    }


    private List<ChatRoomResp> buildContactResp(List<Long> roomIds, Long uid) {
        //load essential room info
        Map<Long, RoomBaseInfo> roomBaseInfo = getRoomBaseInfoMap(roomIds, uid);
        //last msg ids
        List<Long> msgIds = roomBaseInfo.values().stream().map(RoomBaseInfo::getLastMsgId).collect(Collectors.toList());

        List<Message> messages = CollectionUtil.isEmpty(msgIds)?new ArrayList<>():messageDao.listByIds(msgIds);
        Map<Long, Message> msgMap = messages.stream().collect(Collectors.toMap(Message::getId, Function.identity()));
        Map<Long, User> lastMsgUidMap = userInfoCache.getBatch(messages.stream().map(Message::getFromUid).collect(Collectors.toList()));
        //todo unread count
        Map<Long, Integer> userContactUnread = getUnreadCountMap(uid, roomIds);
        return roomBaseInfo.values().stream().map(room->{
            ChatRoomResp chatRoomResp = new ChatRoomResp();
            chatRoomResp.setActiveTime(room.getActiveTime());
            chatRoomResp.setName(room.getName());
            chatRoomResp.setAvatar(room.getAvatar());
            chatRoomResp.setHot_flag(room.getHot_flag());
            chatRoomResp.setType(room.getType());
            chatRoomResp.setRoomId(room.getRoomId());
            Message message = msgMap.get(room.getLastMsgId());
            if(Objects.nonNull(message)){
                AbstractMsgHandler strategyNoNull  = MsgHandlerFactory.getStrategyOrNull(message.getType());
                chatRoomResp.setText(lastMsgUidMap.get(message.getFromUid()).getName()+":"+strategyNoNull.showContactMsg(message));
            }
            chatRoomResp.setUnreadCount(userContactUnread.getOrDefault(room.getRoomId(),0));

            return chatRoomResp;
        }).sorted(Comparator.comparing(ChatRoomResp::getActiveTime).reversed()).collect(Collectors.toList());
    }

    private Map<Long,Integer> getUnreadCountMap(Long uid, List<Long> roomIds) {
        if(Objects.isNull(roomIds)){
            return new HashMap<>();
        }
        List<Contact> contacts = contactDao.getByRoomIdAndUid(uid,roomIds);
        return contacts.parallelStream()
                .map(contact -> Pair.of(contact.getRoomId(),messageDao.getUnReadCount(contact.getRoomId(),contact.getReadTime())))
                .collect(Collectors.toMap(Pair::getKey,Pair::getValue));

    }


    private Map<Long, RoomBaseInfo> getRoomBaseInfoMap(List<Long> roomIds, Long uid) {
        Map<Long, Room> roomMap = roomCache.getBatch(roomIds);

        //group room record based on room type
        Map<Integer, List<Long>> groupInRoomMap = roomMap.values().stream().collect(Collectors.groupingBy(Room::getType,
                Collectors.mapping(Room::getId, Collectors.toList())));
        //acquire room group info
        List<Long> groupRoomId = groupInRoomMap.get(RoomTypeEnums.Group_Room.getRoomType());
        Map<Long, RoomGroup> roomInfoBatch = roomGroupCache.getBatch(groupRoomId);

        //acquire friend info
        List<Long> friendRoomId = groupInRoomMap.get(RoomTypeEnums.FRIEND_ROOM.getRoomType());
        Map<Long, User> friendRoomMap = getFriendRoomMap(friendRoomId, uid);

        return roomMap.values().stream().map(room->{
            RoomBaseInfo roomBaseInfo = new RoomBaseInfo();
            roomBaseInfo.setType(room.getType());
            roomBaseInfo.setRoomId(room.getId());
            roomBaseInfo.setLastMsgId(room.getLastMsgId());
            roomBaseInfo.setActiveTime(room.getActiveTime());
            roomBaseInfo.setHot_flag(room.getHotFlag());
            if(RoomTypeEnums.of(roomBaseInfo.getType())==RoomTypeEnums.Group_Room){
                RoomGroup roomGroup = roomInfoBatch.get(room.getId());
                roomBaseInfo.setAvatar(roomGroup.getAvatar());
                roomBaseInfo.setName(roomGroup.getName());
            } else if (RoomTypeEnums.of(roomBaseInfo.getType())==RoomTypeEnums.FRIEND_ROOM) {
                User user = friendRoomMap.get(room.getId());
                roomBaseInfo.setAvatar(user.getAvatar());
                roomBaseInfo.setName(user.getName());
            }
            return roomBaseInfo;
        }).collect(Collectors.toMap(RoomBaseInfo::getRoomId, Function.identity()));

    }

    private Map<Long, User> getFriendRoomMap(List<Long> friendRoomIds, Long uid) {
        if(CollectionUtil.isEmpty(friendRoomIds)){
            return new HashMap<>();
        }
        Map<Long, RoomFriend> roomFriendMap = roomFriendCache.getBatch(friendRoomIds);
        Set<Long> friendUidList = ChatAdapter.buildFriendUidSet(roomFriendMap.values(), uid);
        Map<Long, User> userInfoMap = userInfoCache.getBatch(new ArrayList<>(friendUidList));
        return roomFriendMap.values()
                .stream()
                .collect(Collectors.toMap(RoomFriend::getRoomId, roomFriend->{
                    Long friendUid = ChatAdapter.getFriendUid(roomFriend, uid);
                    return userInfoMap.get(friendUid);
                }));
    }

    private Double getCursorOrNull(String cursor){
        return Optional.ofNullable(cursor).map(Double::parseDouble).orElse(null);
    }
}
