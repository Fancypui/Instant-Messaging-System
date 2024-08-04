package com.youmin.imsystem.common.user.service.adapter;

import com.youmin.imsystem.common.user.domain.entity.User;
import com.youmin.imsystem.common.user.domain.entity.UserApply;
import com.youmin.imsystem.common.user.domain.entity.UserFriend;
import com.youmin.imsystem.common.user.domain.vo.req.FriendApplyReq;
import com.youmin.imsystem.common.user.domain.vo.resp.FriendApplyResp;
import com.youmin.imsystem.common.user.domain.vo.resp.FriendResp;
import com.youmin.imsystem.common.user.enums.UserApplyReadStatusEnum;
import com.youmin.imsystem.common.user.enums.UserApplyStatusEnum;
import com.youmin.imsystem.common.user.enums.UserApplyTypeEnum;

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

    public static UserApply buildUserApply(FriendApplyReq request, Long uid){
        UserApply userApply = new UserApply();
        userApply.setUid(uid);
        userApply.setTargetId(request.getTargetUid());
        userApply.setType(UserApplyTypeEnum.ADD_FRIEND.getType());
        userApply.setStatus(UserApplyStatusEnum.WAIT_APPROVING.getStatusCode());
        userApply.setMsg(request.getMsg());
        userApply.setReadStatus(UserApplyReadStatusEnum.NOT_YET_READ.getStatusCode());
        return userApply;
    }

    public static List<FriendApplyResp> buildFriendApplyResp(List<UserApply> userApplies){
        List<FriendApplyResp> resp = userApplies.stream().map(userApply -> {
            FriendApplyResp friendApplyResp = new FriendApplyResp();
            friendApplyResp.setAvatar("");
            friendApplyResp.setName("");
            friendApplyResp.setUid(userApply.getUid());
            friendApplyResp.setType(userApply.getType());
            friendApplyResp.setMsg(userApply.getMsg());
            friendApplyResp.setApplyId(userApply.getId());
            friendApplyResp.setStatus(userApply.getStatus());
            return friendApplyResp;
        }).collect(Collectors.toList());
        return resp;
    }
}
