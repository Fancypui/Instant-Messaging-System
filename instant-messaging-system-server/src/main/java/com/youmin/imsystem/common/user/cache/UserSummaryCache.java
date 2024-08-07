package com.youmin.imsystem.common.user.cache;

import cn.hutool.cache.impl.AbstractCache;

import com.youmin.imsystem.common.common.constant.RedisConstant;
import com.youmin.imsystem.common.common.service.cache.AbstractRedisStringCache;
import com.youmin.imsystem.common.user.dao.UserBackpackDao;
import com.youmin.imsystem.common.user.domain.dto.SummaryUserInfoDTO;
import com.youmin.imsystem.common.user.domain.entity.*;
import com.youmin.imsystem.common.user.enums.ItemTypeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class UserSummaryCache extends AbstractRedisStringCache<Long,SummaryUserInfoDTO> {

    @Autowired
    private UserInfoCache userInfoCache;
    @Autowired
    private ItemCache itemCache;

    @Autowired
    private UserBackpackDao userBackpackDao;

    @Override
    public Long expire() {
        return 10*60L;
    }



    @Override
    public String getKey(Long uid) {
        return RedisConstant.getKey(RedisConstant.USER_SUMMARY_STRING,uid);
    }

    @Override
    public Map<Long, SummaryUserInfoDTO> load(List<Long> uidList) {
        //user essential info from user table
        Map<Long, User> batch = userInfoCache.getBatch(uidList);
        //badges item information
        List<ItemConfig> itemConfigs = itemCache.getByType(ItemTypeEnum.BADGES.getType());
        List<Long> itemIds = itemConfigs.stream().map(ItemConfig::getId).collect(Collectors.toList());
        //extract users' item from database
        List<UserBackpack> userBackpackList = userBackpackDao.getByItemIdsAndUidList(uidList,itemIds);
        //group it by uid
        Map<Long, List<UserBackpack>> userBadgesMap = userBackpackList.stream().collect(Collectors.groupingBy(UserBackpack::getUid));

        //convert to summaryDTO
        return uidList.stream().map(uid->{
            User user = batch.get(uid);
            if(Objects.isNull(user)){
                return null;
            }
            SummaryUserInfoDTO dto = new SummaryUserInfoDTO();
            dto.setUid(user.getId());
            dto.setName(user.getName());
            dto.setAvatar(user.getAvatar());
            dto.setWearingItemId(user.getItemId());
            dto.setLocPlace(Optional.ofNullable(user.getIpInfo()).map(IpInfo::getUpdateIpDetail).map(IpDetail::getCity).orElse(null));
            List<UserBackpack> userBadgesList = userBadgesMap.getOrDefault(uid, new ArrayList<>());
            dto.setItemIds(userBadgesList.stream().map(UserBackpack::getItemId).collect(Collectors.toList()));
            return dto;
        }).filter(Objects::nonNull).collect(Collectors.toMap(SummaryUserInfoDTO::getUid,Function.identity()));

    }


}
