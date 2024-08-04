package com.youmin.imsystem.common.user.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.google.common.collect.Lists;
import com.youmin.imsystem.common.chat.domain.entity.RoomFriend;
import com.youmin.imsystem.common.chat.service.IRoomService;
import com.youmin.imsystem.common.common.annotation.RedissonLock;
import com.youmin.imsystem.common.common.domain.enums.YesOrNoEnum;
import com.youmin.imsystem.common.common.domain.vo.req.CursorBaseReq;
import com.youmin.imsystem.common.common.domain.vo.req.PageBaseReq;
import com.youmin.imsystem.common.common.domain.vo.resp.CursorPageBaseResp;
import com.youmin.imsystem.common.common.domain.vo.resp.PageBaseResp;
import com.youmin.imsystem.common.common.event.UserApplyEvent;
import com.youmin.imsystem.common.common.utils.AssertUtils;
import com.youmin.imsystem.common.user.dao.UserApplyDao;
import com.youmin.imsystem.common.user.dao.UserDao;
import com.youmin.imsystem.common.user.dao.UserFriendDao;
import com.youmin.imsystem.common.user.domain.entity.User;
import com.youmin.imsystem.common.user.domain.entity.UserApply;
import com.youmin.imsystem.common.user.domain.entity.UserFriend;
import com.youmin.imsystem.common.user.domain.vo.req.FriendApplyApproveReq;
import com.youmin.imsystem.common.user.domain.vo.req.FriendApplyReq;
import com.youmin.imsystem.common.user.domain.vo.req.FriendCheckReq;
import com.youmin.imsystem.common.user.domain.vo.req.FriendDeleteReq;
import com.youmin.imsystem.common.user.domain.vo.resp.FriendApplyResp;
import com.youmin.imsystem.common.user.domain.vo.resp.FriendCheckResp;
import com.youmin.imsystem.common.user.domain.vo.resp.FriendResp;
import com.youmin.imsystem.common.user.domain.vo.resp.FriendUnreadResp;
import com.youmin.imsystem.common.user.enums.UserApplyStatusEnum;
import com.youmin.imsystem.common.user.service.FriendService;
import com.youmin.imsystem.common.user.service.adapter.FriendAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FriendServiceImpl implements FriendService {

    @Autowired
    private UserFriendDao userFriendDao;
    @Autowired
    private UserDao userDao;

    @Autowired
    private UserApplyDao userApplyDao;

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    @Autowired
    private IRoomService roomService;

    /**
     * Get friendList using cursor pagination
     * @param cursorBaseReq
     * @param uid
     * @return
     */
    @Override
    public CursorPageBaseResp<FriendResp> friendList(CursorBaseReq cursorBaseReq,Long uid) {
        //get friendCursorPage
        CursorPageBaseResp<UserFriend> friendCursorPage = userFriendDao.getFriendPage(cursorBaseReq, uid);
        if(CollectionUtils.isEmpty(friendCursorPage.getList())){
            return  CursorPageBaseResp.empty();
        }
        List<UserFriend> userFriend = friendCursorPage.getList();
        List<Long> friendUidList = userFriend.stream()
                .map(UserFriend::getFriendUid)
                .collect(Collectors.toList());
        //load friend info from user tables
        List<User> allFriends = userDao.getFriendList(friendUidList);
        List<FriendResp> friendRespList = FriendAdapter.buildFriendListResp(userFriend, allFriends);
        return CursorPageBaseResp.init(friendCursorPage,friendRespList);
    }

    /**
     * Batch check if provided uidlist if the friend of current user
     * @param friendCheckReq
     * @param uid
     * @return
     */
    @Override
    public FriendCheckResp friendCheck(FriendCheckReq friendCheckReq, Long uid) {
        List<UserFriend> friendList = userFriendDao.friendListByBatchUid(friendCheckReq.getUidList(),uid);
        Set<Long> friendsUid = friendList.stream().map(UserFriend::getFriendUid).collect(Collectors.toSet());
        List<FriendCheckResp.FriendCheck> friendCheckList = friendCheckReq.getUidList().stream().map(friendUid -> {
            FriendCheckResp.FriendCheck friendCheck = new FriendCheckResp.FriendCheck();
            friendCheck.setUid(friendUid);
            friendCheck.setFriend(friendsUid.contains(friendUid));
            return friendCheck;
        }).collect(Collectors.toList());
        return new FriendCheckResp(friendCheckList);
    }

    @Override
    public void sendFriendRequest(FriendApplyReq request, Long uid) {
        //go userFriend table to check if they are already friend
        UserFriend friend = userFriendDao.getByFriendUid(request.getTargetUid(),uid);
        AssertUtils.isEmpty(friend,"We are friend already");

        //check if current user have send out a friend request before, and it has not been approve
        UserApply myWaitingAcceptUserApply = userApplyDao.getWaitingAcceptUserApply(uid, request.getTargetUid());
        if(Objects.nonNull(myWaitingAcceptUserApply)){
            log.info("Already have friend request, from uid:{}, to target uid",uid, request.getTargetUid());
            return;
        }
        //check if target friend has sent out a friend request to current user before, and it has not been approve
        UserApply targetFriendWaitingAcceptApply = userApplyDao.getWaitingAcceptUserApply(request.getTargetUid(), uid);
        if(Objects.nonNull(targetFriendWaitingAcceptApply)){
            ((FriendService)AopContext.currentProxy()).applyApprove(
                    new FriendApplyApproveReq(targetFriendWaitingAcceptApply.getId()),
                    uid);
            return;
        }
        UserApply insert = FriendAdapter.buildUserApply(request, uid);
        userApplyDao.save(insert);
        //user apply event
        applicationEventPublisher.publishEvent(new UserApplyEvent(this,insert));


    }

    @Override
    @RedissonLock(key = "#uid",waitTime = 1000)
    @Transactional(rollbackFor = Exception.class)
    public void applyApprove(FriendApplyApproveReq request, Long uid) {
        UserApply userApply = userApplyDao.getById(request.getApplyId());
        AssertUtils.isNotEmpty(userApply,"Given apply id is not found");
        AssertUtils.equal(userApply.getTargetId(),uid,"User is not authorized to approve friend request");
        AssertUtils.equal(userApply.getStatus(), UserApplyStatusEnum.WAIT_APPROVING.getStatusCode(),"Cannot approve friend request, " +
                "it has been approved");
        userApplyDao.approveApply(request.getApplyId());//update status of friend requeswt to approve
        createFriend(uid,userApply.getTargetId());
        RoomFriend friendRoom = roomService.createFriendRoom(Arrays.asList(userApply.getTargetId(), userApply.getUid()));

        //todo send a msg to the newly created friend room, notifying users where both of them are friend now can start chatting

    }

    private void createFriend(Long uid, Long friendUid){
        UserFriend save1 = new UserFriend();
        save1.setUid(uid);
        save1.setFriendUid(friendUid);
        save1.setDeleteStatus(YesOrNoEnum.NO.getStatus());
        UserFriend save2 = new UserFriend();
        save2.setUid(uid);
        save2.setFriendUid(friendUid);
        save2.setDeleteStatus(YesOrNoEnum.NO.getStatus());
        userFriendDao.saveBatch(Lists.newArrayList(save1,save2));
    }

    /**
     * user apply unread count
     * @param uid
     * @return
     */
    @Override
    public FriendUnreadResp unread(Long uid) {
        Integer unreadCount = userApplyDao.getUnreadCount(uid);
        return new FriendUnreadResp(unreadCount);
    }

    @Override
    public PageBaseResp<FriendApplyResp> applyPage(PageBaseReq pageBaseReq, Long uid) {
        IPage<UserApply> applyPage = userApplyDao.getApplyPage(pageBaseReq.plugPage(), uid);
        if(CollectionUtils.isEmpty(applyPage.getRecords())){
            return PageBaseResp.empty();
        }
        List<Long> applyIds = applyPage.getRecords().stream().map(UserApply::getId)
                .collect(Collectors.toList());
        userApplyDao.readApply(applyIds,uid);
        return PageBaseResp.init(applyPage,FriendAdapter.buildFriendApplyResp(applyPage.getRecords()));
    }

    /**
     * delete friend request
     * @param request
     * @param uid
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteFriend(FriendDeleteReq request, Long uid) {
        List<UserFriend> userFriendList = userFriendDao.getByUidAndFriendUid(uid, request.getFriendUid());
        if(CollectionUtil.isEmpty(userFriendList)){
            log.info("No friend relationship,uid:{} friendUid:{}",uid,request.getFriendUid());
            return;
        }
        List<Long> userFriendIds = userFriendList.stream().map(UserFriend::getId).collect(Collectors.toList());
        userFriendDao.removeByIds(userFriendIds);
        //disable room
        roomService.disableFriendRoom(Arrays.asList(uid,request.getFriendUid()));

    }


}
