package com.youmin.imsystem.common.common.event.listener;

import com.youmin.imsystem.common.common.event.UserOfflineEvent;
import com.youmin.imsystem.common.user.cache.UserCache;
import com.youmin.imsystem.common.user.dao.UserDao;
import com.youmin.imsystem.common.user.domain.entity.User;
import com.youmin.imsystem.common.user.enums.ChatActiveStatusEnum;
import com.youmin.imsystem.common.user.service.adapter.WSAdapter;
import com.youmin.imsystem.common.user.service.impl.PushService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class UserOfflineListener {
    @Autowired
    private UserDao userDao;

    @Autowired
    private PushService pushService;

    @Autowired
    private UserCache userCache;

    @Autowired
    private WSAdapter wsAdapter;

    @Async
    @EventListener(classes = UserOfflineEvent.class)
    public void saveRedisAndPush(UserOfflineEvent event) {
        User user = event.getUser();
        userCache.offline(user.getId(), user.getLastOptTime());
        pushService.pushService(wsAdapter.buildOfflineNotifyResp(user));
    }

    @Async
    @EventListener(UserOfflineEvent.class)
    public void saveDB(UserOfflineEvent event){
        User user = event.getUser();
        User update = new User();
        update.setId(user.getId());
        update.setLastOptTime(user.getLastOptTime());
        update.setActiveStatus(ChatActiveStatusEnum.OFFLINE.getStatus());
        userDao.updateById(update);
    }
}
