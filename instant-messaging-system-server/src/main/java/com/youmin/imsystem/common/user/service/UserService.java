package com.youmin.imsystem.common.user.service;

import com.youmin.imsystem.common.user.domain.entity.User;
import com.youmin.imsystem.common.user.domain.vo.req.ModifyNameReq;
import com.youmin.imsystem.common.user.domain.vo.req.WearingBadgeReq;
import com.youmin.imsystem.common.user.domain.vo.resp.BadgesResp;
import com.youmin.imsystem.common.user.domain.vo.resp.UserInfoResp;
import me.chanjar.weixin.common.bean.WxOAuth2UserInfo;

import java.util.List;


public interface UserService {

    void registered(User user);

    UserInfoResp getUserInfo(Long uid);

    void modifyName(Long uid, String name);

    List<BadgesResp> badges(Long uid);

    void wearingBadge(Long uid, WearingBadgeReq wearingBadgeReq);
}
