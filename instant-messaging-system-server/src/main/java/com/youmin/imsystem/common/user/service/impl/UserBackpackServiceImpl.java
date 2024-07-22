package com.youmin.imsystem.common.user.service.impl;

import cn.hutool.extra.spring.SpringUtil;
import com.youmin.imsystem.common.common.annotation.RedissonLock;
import com.youmin.imsystem.common.common.domain.enums.IdempotentEnum;
import com.youmin.imsystem.common.common.domain.enums.YesOrNoEnum;
import com.youmin.imsystem.common.user.dao.UserBackpackDao;
import com.youmin.imsystem.common.user.domain.entity.UserBackpack;
import com.youmin.imsystem.common.user.service.IUserBackpackService;
import org.apache.catalina.core.ApplicationContext;
import org.springframework.context.annotation.Lazy;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class UserBackpackServiceImpl implements IUserBackpackService {
    @Autowired
    private RedissonClient redissonClient;
    @Autowired
    private UserBackpackDao userBackpackDao;

    @Autowired
    @Lazy
    private UserBackpackServiceImpl userBackpackService;

    @Override
    public void acquireItem(Long uid, Long itemId, IdempotentEnum idempotentEnum, String businessId) {
        //build idempotent string
        String idempotent = getIdempotent(itemId, idempotentEnum, businessId);

        userBackpackService.doAcquire(uid,itemId,idempotent);
    }

    /**
     * if two or more threads with same idempotent string reach this method at the same time
     * the successors requires to wait for previous threads to finish executing business logic
     * to acheive idempotent
     * @param uid
     * @param itemId
     * @param idempotent
     */
    @RedissonLock(key = "#idempotent",waitTime = 5000)
    public void doAcquire(Long uid,Long itemId,String idempotent){
        UserBackpack item = userBackpackDao.getItemByIdempotent(idempotent);
        //idempotent check
        if(Objects.nonNull(item)){
            return;
        }
        //give item to provided uid
        UserBackpack insert = UserBackpack.builder()
                .itemId(itemId)
                .uid(uid)
                .status(YesOrNoEnum.NO.getStatus())
                .idempotent(idempotent)
                .build();
        userBackpackDao.save(insert);
    }

    private String getIdempotent(Long itemId, IdempotentEnum idempotentEnum, String businessId){
        return String.format("%d_%d_%s",itemId,idempotentEnum.getType(),businessId);
    }
}
