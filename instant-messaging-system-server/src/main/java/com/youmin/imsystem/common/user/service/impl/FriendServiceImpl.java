package com.youmin.imsystem.common.user.service.impl;

import com.youmin.imsystem.common.common.domain.vo.req.CursorBaseReq;
import com.youmin.imsystem.common.common.domain.vo.resp.CursorPageBaseResp;
import com.youmin.imsystem.common.user.dao.UserDao;
import com.youmin.imsystem.common.user.dao.UserFriendDao;
import com.youmin.imsystem.common.user.domain.entity.User;
import com.youmin.imsystem.common.user.domain.entity.UserFriend;
import com.youmin.imsystem.common.user.domain.vo.resp.FriendResp;
import com.youmin.imsystem.common.user.service.FriendService;
import com.youmin.imsystem.common.user.service.adapter.FriendAdapter;
import jodd.util.CollectionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FriendServiceImpl implements FriendService {

    @Autowired
    private UserFriendDao userFriendDao;
    @Autowired
    private UserDao userDao;

    @Override
    public CursorPageBaseResp<FriendResp> friendList(CursorBaseReq cursorBaseReq,Long uid) {
        CursorPageBaseResp<UserFriend> friendCursorPage = userFriendDao.getFriendPage(cursorBaseReq, uid);
        if(CollectionUtils.isEmpty(friendCursorPage.getList())){
            return  CursorPageBaseResp.empty();
        }
        List<UserFriend> userFriend = friendCursorPage.getList();
        List<Long> friendUidList = userFriend.stream()
                .map(UserFriend::getFriendUid)
                .collect(Collectors.toList());
        List<User> allFriends = userDao.getFriendList(friendUidList);
        List<FriendResp> friendRespList = FriendAdapter.buildFriendListResp(userFriend, allFriends);
        return CursorPageBaseResp.init(friendCursorPage,friendRespList);
    }
}
