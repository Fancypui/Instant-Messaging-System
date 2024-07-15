package com.youmin.imsystem.common.user.service;

import com.youmin.imsystem.common.user.domain.entity.User;
import me.chanjar.weixin.common.bean.WxOAuth2UserInfo;


public interface UserService {

    Long registered(User user);


}
