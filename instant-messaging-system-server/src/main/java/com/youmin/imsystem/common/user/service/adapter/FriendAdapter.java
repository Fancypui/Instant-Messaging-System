package com.youmin.imsystem.common.user.service.adapter;

import com.youmin.imsystem.common.user.domain.entity.User;
import com.youmin.imsystem.common.user.domain.entity.UserFriend;
import com.youmin.imsystem.common.user.domain.vo.resp.FriendResp;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

public class FriendAdapter {

    public static List<FriendResp> buildFriendListResp(List<UserFriend> userFriends, List<User> users){
        Map<Long, User> userMap = users.stream()
                .collect(Collectors.toMap(User::getId, Function.identity()));
        return userFriends.stream()
                .map(userFriend -> {
                    User user = userMap.get(userFriend.getFriendUid());
                    FriendResp friendResp = new FriendResp();
                    friendResp.setUid(userFriend.getFriendUid());
                    if(Objects.nonNull(user)) {
                        friendResp.setActiveStatus(user.getActiveStatus());
                        friendResp.setAvatar(user.getAvatar());
                        friendResp.setName(user.getName());
                    }
                    return friendResp;
                }).collect(Collectors.toList());
    }
}
