package com.youmin.imsystem.common.user.service.impl;

import cn.hutool.core.util.StrUtil;
import com.youmin.imsystem.common.common.event.UserBlackEvent;
import com.youmin.imsystem.common.common.event.UserRegisteredEvent;
import com.youmin.imsystem.common.common.utils.AssertUtils;
import com.youmin.imsystem.common.user.cache.ItemCache;
import com.youmin.imsystem.common.user.cache.UserCache;
import com.youmin.imsystem.common.user.cache.UserSummaryCache;
import com.youmin.imsystem.common.user.dao.BlackDao;
import com.youmin.imsystem.common.user.dao.ItemConfigDao;
import com.youmin.imsystem.common.user.dao.UserBackpackDao;
import com.youmin.imsystem.common.user.dao.UserDao;
import com.youmin.imsystem.common.user.domain.dto.ItemInfoDTO;
import com.youmin.imsystem.common.user.domain.dto.SummaryUserInfoDTO;
import com.youmin.imsystem.common.user.domain.entity.*;
import com.youmin.imsystem.common.user.domain.vo.req.ItemInfoReq;
import com.youmin.imsystem.common.user.domain.vo.req.SummaryUserInfoReq;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
    private UserCache userCache;

    @Autowired
    private UserSummaryCache userSummaryCache;

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
            //delete cache
            userCache.userInfoChange(uid);
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
        //delete cache
        userCache.userInfoChange(uid);
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

    /**
     * get item info (badges) from spring cache (caffeine)
     *
     * @param request
     * @return
     */
    @Override
    public List<ItemInfoDTO> getItemInfo(ItemInfoReq request) {
        return request.getItemInfoList().stream()
                .map(itemInfo -> {
                    ItemConfig itemConfig = itemCache.getById(itemInfo.getItemId());
                    if(Objects.nonNull(itemInfo.getLastModifyTime())&&itemConfig.getUpdateTime().getTime()<=itemInfo.getLastModifyTime()){
                        return ItemInfoDTO.skip(itemInfo.getItemId());
                    }
                    //convert to itemInfoDTO
                    ItemInfoDTO itemInfoDTO = new ItemInfoDTO();
                    itemInfoDTO.setNeedRefresh(Boolean.TRUE);
                    itemInfoDTO.setItemId(itemConfig.getId());
                    itemInfoDTO.setImg(itemConfig.getImg());
                    itemInfoDTO.setDesc(itemConfig.getDescribe());
                    return itemInfoDTO;
                }).collect(Collectors.toList());

    }

    @Override
    public List<SummaryUserInfoDTO> getSummaryUserInfo(SummaryUserInfoReq request) {
        //get the uid that requiring to be refresh
       List<Long> needRefreshUid =  getNeedSyncUidList(request.getUserInfoReqList());
       //load user summary info
       Map<Long,SummaryUserInfoDTO> summaryUserInfo = userSummaryCache.getBatch(needRefreshUid);
       return request.getUserInfoReqList().stream()
               .map(a->{
                   if(!summaryUserInfo.containsKey(a.getUid())){
                       return SummaryUserInfoDTO.skip(a.getUid());
                   }
                   return summaryUserInfo.get(a.getUid());
               }).collect(Collectors.toList());
    }

    private List<Long> getNeedSyncUidList(List<SummaryUserInfoReq.UserInfoReq> reqList) {
        ArrayList<Long> needSyncUidList = new ArrayList<>();
        List<Long> userModifyTime = userCache.
                getUserModifyTime(reqList.stream().map(SummaryUserInfoReq.UserInfoReq::getUid).collect(Collectors.toList()));
        for (int i = 0; i < reqList.size(); i++) {
            SummaryUserInfoReq.UserInfoReq userInfoReq = reqList.get(i);
            Long modifyTime = userModifyTime.get(i);
            if(Objects.isNull(userInfoReq.getLastModifyTime())||(Objects.nonNull(modifyTime)&&modifyTime>userInfoReq.getLastModifyTime())){
                needSyncUidList.add(userInfoReq.getUid());
            }
        }
        return needSyncUidList;
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
