package com.youmin.imsystem.common.user.service;

import com.youmin.imsystem.common.user.domain.entity.User;
import com.youmin.imsystem.common.user.domain.vo.req.ModifyNameReq;
import com.youmin.imsystem.common.user.domain.vo.resp.UserInfoResp;
import me.chanjar.weixin.common.bean.WxOAuth2UserInfo;


public interface UserService {

    Long registered(User user);

    UserInfoResp getUserInfo(Long uid);

    void modifyName(Long uid,ModifyNameReq modifyNameReq);
}
