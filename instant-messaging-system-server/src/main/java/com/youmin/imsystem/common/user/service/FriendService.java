package com.youmin.imsystem.common.user.service;

import com.youmin.imsystem.common.common.domain.vo.req.CursorBaseReq;
import com.youmin.imsystem.common.common.domain.vo.req.PageBaseReq;
import com.youmin.imsystem.common.common.domain.vo.resp.CursorPageBaseResp;
import com.youmin.imsystem.common.common.domain.vo.resp.PageBaseResp;
import com.youmin.imsystem.common.user.domain.vo.req.FriendApplyApproveReq;
import com.youmin.imsystem.common.user.domain.vo.req.FriendApplyReq;
import com.youmin.imsystem.common.user.domain.vo.req.FriendCheckReq;
import com.youmin.imsystem.common.user.domain.vo.req.FriendDeleteReq;
import com.youmin.imsystem.common.user.domain.vo.resp.FriendApplyResp;
import com.youmin.imsystem.common.user.domain.vo.resp.FriendCheckResp;
import com.youmin.imsystem.common.user.domain.vo.resp.FriendResp;
import com.youmin.imsystem.common.user.domain.vo.resp.FriendUnreadResp;

public interface FriendService {
    CursorPageBaseResp<FriendResp> friendList(CursorBaseReq cursorBaseReq,Long uid);

    FriendCheckResp friendCheck(FriendCheckReq friendCheckReq, Long uid);

    void sendFriendRequest(FriendApplyReq request, Long uid);

    void applyApprove(FriendApplyApproveReq request, Long uid);

    FriendUnreadResp unread(Long uid);

    PageBaseResp<FriendApplyResp> applyPage(PageBaseReq pageBaseReq, Long uid);

    void deleteFriend(FriendDeleteReq request, Long uid);
}
