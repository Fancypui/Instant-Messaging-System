package com.youmin.imsystem.common.user.service;

import com.youmin.imsystem.common.user.domain.dto.ItemInfoDTO;
import com.youmin.imsystem.common.user.domain.dto.SummaryUserInfoDTO;
import com.youmin.imsystem.common.user.domain.entity.User;
import com.youmin.imsystem.common.user.domain.vo.req.ItemInfoReq;
import com.youmin.imsystem.common.user.domain.vo.req.SummaryUserInfoReq;
import com.youmin.imsystem.common.user.domain.vo.req.UserBlackReq;
import com.youmin.imsystem.common.user.domain.vo.req.WearingBadgeReq;
import com.youmin.imsystem.common.user.domain.vo.resp.BadgesResp;
import com.youmin.imsystem.common.user.domain.vo.resp.UserInfoResp;

import java.util.List;


public interface UserService {

    void registered(User user);

    UserInfoResp getUserInfo(Long uid);

    void modifyName(Long uid, String name);

    List<BadgesResp> badges(Long uid);

    void wearingBadge(Long uid, WearingBadgeReq wearingBadgeReq);

    void black(UserBlackReq userBlackReq);

    List<ItemInfoDTO> getItemInfo(ItemInfoReq request);

    List<SummaryUserInfoDTO> getSummaryUserInfo(SummaryUserInfoReq request);
}
