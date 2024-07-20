package com.youmin.imsystem.common.user.service.adapter;

import com.youmin.imsystem.common.user.domain.entity.User;
import com.youmin.imsystem.common.user.domain.vo.resp.UserInfoResp;
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

    public static UserInfoResp buildUserInfoResp(User user, Integer modifyNameCardCount){
        UserInfoResp userInfoResp = new UserInfoResp();
        userInfoResp.setId(user.getId());
        userInfoResp.setSex(user.getSex());
        userInfoResp.setAvatar(user.getAvatar());
        userInfoResp.setModifyNameChance(modifyNameCardCount);
        userInfoResp.setName(user.getName());
        return userInfoResp;
    }


}
