package com.youmin.imsystem.common.chat.service.adapter;

import com.youmin.imsystem.common.chat.domain.entity.Contact;
import com.youmin.imsystem.common.chat.domain.vo.response.ChatMessageReadResp;


import java.util.List;
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
}
