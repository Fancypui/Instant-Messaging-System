package com.youmin.imsystem.common.user.service.adapter;

import cn.hutool.core.bean.BeanUtil;
import com.youmin.imsystem.common.common.domain.enums.YesOrNoEnum;
import com.youmin.imsystem.common.user.domain.entity.ItemConfig;
import com.youmin.imsystem.common.user.domain.entity.User;
import com.youmin.imsystem.common.user.domain.entity.UserBackpack;
import com.youmin.imsystem.common.user.domain.vo.resp.BadgesResp;
import com.youmin.imsystem.common.user.domain.vo.resp.UserInfoResp;
import me.chanjar.weixin.common.bean.WxOAuth2UserInfo;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

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

    public static List<BadgesResp> buildBadgeResp(List<ItemConfig> allBadges, List<UserBackpack> userObtainedItems, User user){
        Set<Long> itemIds = userObtainedItems.stream().map(UserBackpack::getItemId).collect(Collectors.toSet());
        return allBadges.stream().map(itemConfig -> {
            BadgesResp badgesResp = new BadgesResp();
            BeanUtil.copyProperties(itemConfig, badgesResp);
            badgesResp.setObtained(itemIds.contains(itemConfig.getId()) ? YesOrNoEnum.YES.getStatus() : YesOrNoEnum.NO.getStatus());
            badgesResp.setWearing(Objects.equals(itemConfig.getId(), user.getItemId()) ? YesOrNoEnum.YES.getStatus() : YesOrNoEnum.NO.getStatus());
            return badgesResp;
        }).sorted(Comparator.comparing(BadgesResp::getWearing).reversed()
                .thenComparing(Comparator.comparing(BadgesResp::getObtained).reversed())).collect(Collectors.toList());

    }


}
