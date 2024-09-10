package com.youmin.imsystem.common.chat.service.adapter;

import com.youmin.imsystem.common.chat.domain.entity.Contact;
import com.youmin.imsystem.common.chat.domain.entity.GroupMember;
import com.youmin.imsystem.common.chat.domain.entity.RoomGroup;
import com.youmin.imsystem.common.chat.domain.enums.GroupRoleAppEnum;
import com.youmin.imsystem.common.chat.domain.enums.MessageTypeEnum;
import com.youmin.imsystem.common.chat.domain.vo.request.ChatMessageReq;
import com.youmin.imsystem.common.chat.domain.vo.response.ChatMessageReadResp;
import com.youmin.imsystem.common.user.domain.entity.User;


import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RoomAdapter {

    public static List<ChatMessageReadResp> buildReadResp(List<Contact> list){
        return list.stream()
                .map(contact -> {
                    ChatMessageReadResp chatMessageReadResp = new ChatMessageReadResp();
                    chatMessageReadResp.setUid(contact.getUid());
                    return chatMessageReadResp;
                }).collect(Collectors.toList());
    }

    public static List<GroupMember> buildGroupMemberBatch(List<Long> uidList, Long groupId) {
        return uidList.stream()
                .distinct()
                .map(uid->{
                    GroupMember groupMember = new GroupMember();
                    groupMember.setGroupId(groupId);
                    groupMember.setRole(GroupRoleAppEnum.MEMBER.getType());
                    groupMember.setUid(uid);
                    return groupMember;
                }).collect(Collectors.toList());
    }

    public static ChatMessageReq buildGroupAddMessage(RoomGroup roomGroup, User user, Map<Long, User> member) {
        ChatMessageReq chatMessageReq = new ChatMessageReq();
        chatMessageReq.setRoomId(roomGroup.getRoomId());
        chatMessageReq.setMsgType(MessageTypeEnum.SYSTEM.getMsgType());
        StringBuilder sb = new StringBuilder();
        sb.append("\"")
                .append(user.getName())
                .append("\"")
                .append(" invite ")
                .append(member.values().stream().map(u -> "\"" + u.getName() + "\"").collect(Collectors.joining(",")))
                .append("to new group");
        chatMessageReq.setBody(sb.toString());
        return chatMessageReq;
    }
}
