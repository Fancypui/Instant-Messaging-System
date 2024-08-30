package com.youmin.imsystem.common.chat.service;

import com.youmin.imsystem.common.chat.domain.vo.request.ContactFriendReq;
import com.youmin.imsystem.common.chat.domain.vo.request.MemberReq;
import com.youmin.imsystem.common.chat.domain.vo.response.ChatMemberResp;
import com.youmin.imsystem.common.chat.domain.vo.response.ChatRoomResp;
import com.youmin.imsystem.common.common.domain.vo.req.CursorBaseReq;
import com.youmin.imsystem.common.common.domain.vo.req.IdReqVO;
import com.youmin.imsystem.common.common.domain.vo.resp.CursorPageBaseResp;

public interface RoomAppService {

    CursorPageBaseResp<ChatRoomResp> getRoomPage(CursorBaseReq request,Long uid);

    ChatRoomResp getContactDetail(IdReqVO request, Long uid);

    ChatRoomResp getContactDetailByFriend(ContactFriendReq request, Long uid);

    CursorPageBaseResp<ChatMemberResp> getMemberPage(MemberReq req);
}
