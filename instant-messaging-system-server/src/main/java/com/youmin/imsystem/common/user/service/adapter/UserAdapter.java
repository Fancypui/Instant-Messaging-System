package com.youmin.imsystem.common.user.service.adapter;

import com.youmin.imsystem.common.user.domain.entity.User;
import me.chanjar.weixin.common.bean.WxOAuth2UserInfo;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;

/**
 * User adapter
 */
public class UserAdapter {
    public static User buildUser(String openId){
        return User.builder().openId(openId).build();
    }

    public static User buildAuthorizedUser(WxOAuth2UserInfo userInfo,Long uid){
        return User.builder()
                .id(uid)
                .name(userInfo.getNickname())
                .avatar(userInfo.getHeadImgUrl()).build();
    }


}
