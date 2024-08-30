package com.youmin.imsystem.common.chat.service.adapter;

import com.youmin.imsystem.common.chat.domain.vo.response.ChatMemberResp;
import com.youmin.imsystem.common.user.domain.entity.User;

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
}
