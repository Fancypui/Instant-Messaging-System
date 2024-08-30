package com.youmin.imsystem.common.common.event.listener;

import com.youmin.imsystem.common.common.event.UserOnlineEvent;
import com.youmin.imsystem.common.user.cache.UserCache;
import com.youmin.imsystem.common.user.dao.UserDao;
import com.youmin.imsystem.common.user.domain.entity.User;
import com.youmin.imsystem.common.user.enums.UserActiveStatusEnum;
import com.youmin.imsystem.common.user.service.IpService;
import com.youmin.imsystem.common.user.service.adapter.WSAdapter;
import com.youmin.imsystem.common.user.service.impl.PushService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class UserOnlineListener {

    @Autowired
    private UserDao userDao;

    @Autowired
    private UserCache userCache;

    @Autowired
    private IpService ipService;
    @Autowired
    private PushService pushService;

    @Autowired
    private WSAdapter wsAdapter;

    @Async
    @EventListener(value = UserOnlineEvent.class)
    public void saveRedisAndPush(UserOnlineEvent event){
        User user = event.getUser();
        userCache.online(user.getId(),user.getLastOptTime());
        pushService.pushService(wsAdapter.buildOnlineNotifyResp(user));
    }

    @Async
    @TransactionalEventListener(value = UserOnlineEvent.class, phase = TransactionPhase.AFTER_COMMIT,fallbackExecution = true)
    public void saveDB(UserOnlineEvent userOnlineEvent){
        User user = userOnlineEvent.getUser();
        User update = new User();
        update.setLastOptTime(user.getLastOptTime());
        update.setIpInfo(user.getIpInfo());
        update.setId(user.getId());
        update.setActiveStatus(UserActiveStatusEnum.ONLINE.getType());
        userDao.updateById(update);
        ipService.refreshIpDetail(user.getId());
    }
}
