package com.youmin.imsystem.common.chat.service;

import com.youmin.imsystem.common.chat.domain.vo.request.*;
import com.youmin.imsystem.common.chat.domain.vo.response.ChatMemberListResp;
import com.youmin.imsystem.common.chat.domain.vo.response.ChatMemberResp;
import com.youmin.imsystem.common.chat.domain.vo.response.ChatRoomResp;
import com.youmin.imsystem.common.chat.domain.vo.response.MemberResp;
import com.youmin.imsystem.common.common.domain.vo.req.CursorBaseReq;
import com.youmin.imsystem.common.common.domain.vo.req.IdReqVO;
import com.youmin.imsystem.common.common.domain.vo.resp.CursorPageBaseResp;

import java.util.List;

public interface RoomAppService {

    CursorPageBaseResp<ChatRoomResp> getRoomPage(CursorBaseReq request,Long uid);

    ChatRoomResp getContactDetail(IdReqVO request, Long uid);

    ChatRoomResp getContactDetailByFriend(ContactFriendReq request, Long uid);

    CursorPageBaseResp<ChatMemberResp> getMemberPage(MemberReq req);

    MemberResp getGroupDetail(IdReqVO request, Long uid);

    List<ChatMemberListResp> getMemberList(MemberReq request);

    Long addGroup(GroupAddReq request, Long uid);

    void addMember(MemberAddReq request, Long uid);

    void delMember(MemberDelReq request, Long uid);
}
