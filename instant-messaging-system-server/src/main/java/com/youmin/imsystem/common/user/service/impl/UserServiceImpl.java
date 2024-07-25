package com.youmin.imsystem.common.user.service.impl;

import cn.hutool.core.util.StrUtil;
import com.youmin.imsystem.common.common.annotation.RedissonLock;
import com.youmin.imsystem.common.common.event.UserBlackEvent;
import com.youmin.imsystem.common.common.event.UserRegisteredEvent;
import com.youmin.imsystem.common.common.utils.AssertUtils;
import com.youmin.imsystem.common.user.cache.ItemCache;
import com.youmin.imsystem.common.user.dao.BlackDao;
import com.youmin.imsystem.common.user.dao.ItemConfigDao;
import com.youmin.imsystem.common.user.dao.UserBackpackDao;
import com.youmin.imsystem.common.user.dao.UserDao;
import com.youmin.imsystem.common.user.domain.entity.*;
import com.youmin.imsystem.common.user.domain.vo.req.ModifyNameReq;
import com.youmin.imsystem.common.user.domain.vo.req.UserBlackReq;
import com.youmin.imsystem.common.user.domain.vo.req.WearingBadgeReq;
import com.youmin.imsystem.common.user.domain.vo.resp.BadgesResp;
import com.youmin.imsystem.common.user.domain.vo.resp.UserInfoResp;
import com.youmin.imsystem.common.user.enums.BlackTypeEnum;
import com.youmin.imsystem.common.user.enums.ItemEnum;
import com.youmin.imsystem.common.user.enums.ItemTypeEnum;
import com.youmin.imsystem.common.user.service.UserService;
import com.youmin.imsystem.common.user.service.adapter.UserAdapter;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.bean.WxOAuth2UserInfo;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import org.apache.velocity.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private UserBackpackDao userBackpackDao;

    @Autowired
    private ItemCache itemCache;

    @Autowired
    private ItemConfigDao itemConfigDao;
    @Autowired
    private BlackDao blackDao;

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;





    @Override
    @Transactional
    public void registered(User user) {
        userDao.save(user);
        //emit user registration event to subscribers who subscribe this user registered event
        applicationEventPublisher.publishEvent(new UserRegisteredEvent(this,user));
    }

    /**
     * get user info service
     * @param uid
     * @return
     */
    @Override
    public UserInfoResp getUserInfo(Long uid) {
        User user = userDao.getById(uid);
        Integer count =
                userBackpackDao.getModifyNameCardCount(ItemEnum.MODIFY_NAME_CARD.getItemId(), uid);
        return UserAdapter.buildUserInfoResp(user,count);
    }

    @Override
    @Transactional
    public void modifyName(Long uid, String name) {
        User oldUser = userDao.getByName(name);
        AssertUtils.isEmpty(oldUser,"Username already occupied, please change another name");
        UserBackpack modifyNameCard = userBackpackDao.getFirstValidItem(uid,ItemEnum.MODIFY_NAME_CARD.getItemId());
        AssertUtils.isNotEmpty(modifyNameCard,"User does not have a modify name card");
        /**
         * set the status of item to invalid since it is already been used
         */
        if(userBackpackDao.useItem(modifyNameCard)){
            userDao.modifyName(uid,name);
        }

    }

    /**
     * Get user badges with non acquired badges, acquired badges, and wearing
     * @param uid
     * @return
     */
    @Override
    public List<BadgesResp> badges(Long uid) {
        //query all badges
        List<ItemConfig> badges = itemCache.getByType(ItemTypeEnum.BADGES.getType());
        //get all badges
        List<Long> itemIds = badges.stream().map(ItemConfig::getId).collect(Collectors.toList());
        //get user obtained badges
        List<UserBackpack> userObtainedItems = userBackpackDao.getObtainedItems(uid, itemIds);
        //get user wearing badges
        User user = userDao.getById(uid);
        return UserAdapter.buildBadgeResp(badges, userObtainedItems, user);
    }

    @Override
    public void wearingBadge(Long uid, WearingBadgeReq wearingBadgeReq) {
        //query selected item
        ItemConfig item = itemConfigDao.getById(wearingBadgeReq.getItemId());
        //check selected item is badge
        AssertUtils.equal(item.getType(),ItemTypeEnum.BADGES.getType(), "Provided item id is not badge");
        //check if user has the selected badge
        UserBackpack userBackpack = userBackpackDao.getFirstValidItem(uid, wearingBadgeReq.getItemId());
        AssertUtils.isNotEmpty(userBackpack,"User does not have that badge");
        //wearing badge
        userDao.wearBadge(uid, wearingBadgeReq.getItemId());
    }

    @Override
    @Transactional
    public void black(UserBlackReq userBlackReq) {
        Black black = new Black();
        black.setType(BlackTypeEnum.UID.getType());
        black.setTarget(userBlackReq.getUid().toString());
        blackDao.save(black);
        User user = userDao.getById(userBlackReq.getUid());
        blackIP(Optional.ofNullable(user.getIpInfo()).map(ipInfo -> ipInfo.getCreateIp()).orElse(null));
        blackIP(Optional.ofNullable(user.getIpInfo()).map(ipInfo -> ipInfo.getUpdateIp()).orElse(null));
        applicationEventPublisher.publishEvent(new UserBlackEvent(this,user));
    }

    private void blackIP(String ip) {
        if(StrUtil.isBlank(ip)){
            return;
        }
        try {
            Black insert = new Black();
            insert.setTarget(ip);
            insert.setType(BlackTypeEnum.IP.getType());
            blackDao.save(insert);
        }catch(Exception e){
            log.error("duplicate black ip:{}", ip);
        }
    }


}
