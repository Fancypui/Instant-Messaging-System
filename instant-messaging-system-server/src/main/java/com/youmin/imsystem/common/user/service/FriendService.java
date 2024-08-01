package com.youmin.imsystem.common.user.service;

import com.youmin.imsystem.common.common.domain.vo.req.CursorBaseReq;
import com.youmin.imsystem.common.common.domain.vo.resp.CursorPageBaseResp;
import com.youmin.imsystem.common.user.domain.vo.resp.FriendResp;

public interface FriendService {
    CursorPageBaseResp<FriendResp> friendList(CursorBaseReq cursorBaseReq,Long uid);
}
