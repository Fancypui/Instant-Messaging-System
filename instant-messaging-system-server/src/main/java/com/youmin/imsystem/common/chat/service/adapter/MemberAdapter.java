package com.youmin.imsystem.common.chat.service.adapter;

import com.youmin.imsystem.common.chat.domain.entity.GroupMember;
import com.youmin.imsystem.common.chat.domain.enums.GroupRoleAppEnum;
import com.youmin.imsystem.common.chat.domain.vo.response.ChatMemberListResp;
import com.youmin.imsystem.common.chat.domain.vo.response.ChatMemberResp;
import com.youmin.imsystem.common.user.domain.entity.User;
import com.youmin.imsystem.common.user.domain.vo.resp.WSMemberChange;
import com.youmin.imsystem.common.user.domain.vo.resp.WSRespBase;
import com.youmin.imsystem.common.user.enums.WSRespTypeEnum;

import java.util.List;
import java.util.stream.Collectors;

public class MemberAdapter {
    public static List<ChatMemberResp> buildChatMemberResp(List<User> list) {
        return list.stream()
                .map(user -> {
                    ChatMemberResp chatMemberResp = new ChatMemberResp();
                    chatMemberResp.setUid(user.getId());
                    chatMemberResp.setLastOptTime(user.getLastOptTime());
                    chatMemberResp.setActiveStatus(user.getActiveStatus());
                    return chatMemberResp;
                }).collect(Collectors.toList());
    }

    public static List<ChatMemberListResp> buiidMemberListResp(List<User> memberList) {
        return memberList
                .stream()
                .map(user ->{
                    ChatMemberListResp chatMemberListResp = new ChatMemberListResp();
                    chatMemberListResp.setAvatar(user.getAvatar());
                    chatMemberListResp.setUid(user.getId());
                    chatMemberListResp.setName(user.getName());
                    return chatMemberListResp;
                }).collect(Collectors.toList());
    }

    public static WSRespBase<WSMemberChange> buildMemberAddWS(Long roomId, User user) {
        WSRespBase<WSMemberChange> wsBaseResp = new WSRespBase<>();
        wsBaseResp.setType(WSRespTypeEnum.MEMBER_CHANGE.getType());
        WSMemberChange wsMemberChange = new WSMemberChange();
        wsMemberChange.setActiveStatus(user.getActiveStatus());
        wsMemberChange.setLastOptTome(user.getLastOptTime());
        wsMemberChange.setUid(user.getId());
        wsMemberChange.setRoomId(roomId);
        wsMemberChange.setChangeType(WSMemberChange.CHANGE_TYPE_ADD);
        wsBaseResp.setData(wsMemberChange);
        return wsBaseResp;
    }

    public static List<GroupMember> buildMemberAdd(Long groupId, List<Long> waitAddUidList) {
        return waitAddUidList.stream().map(uid->{
            GroupMember groupMember = new GroupMember();
            groupMember.setGroupId(groupId);
            groupMember.setUid(uid);
            groupMember.setRole(GroupRoleAppEnum.MEMBER.getType());
            return groupMember;
        }).collect(Collectors.toList());
    }

    public static WSRespBase<WSMemberChange> buildMemberRemoveWS(Long roomId, Long uid) {
        WSRespBase<WSMemberChange> wsBaseResp = new WSRespBase<>();
        wsBaseResp.setType(WSRespTypeEnum.MEMBER_CHANGE.getType());
        WSMemberChange wsMemberChange = new WSMemberChange();
        wsMemberChange.setUid(uid);
        wsMemberChange.setRoomId(roomId);
        wsMemberChange.setChangeType(WSMemberChange.CHANGE_TYPE_REMOVE);
        wsBaseResp.setData(wsMemberChange);
        return wsBaseResp;
    }
}
