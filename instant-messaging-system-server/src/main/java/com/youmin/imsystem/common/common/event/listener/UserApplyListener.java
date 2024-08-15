package com.youmin.imsystem.common.common.event.listener;

import com.youmin.imsystem.common.common.event.UserApplyEvent;
import com.youmin.imsystem.common.user.dao.UserApplyDao;
import com.youmin.imsystem.common.user.domain.entity.UserApply;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class UserApplyListener {

    @Autowired
    private UserApplyDao userApplyDao;

    @Async
    @TransactionalEventListener(value = UserApplyEvent.class, fallbackExecution = true)
    public void sendMsg(UserApplyEvent userApplyEvent){
        UserApply userApply = userApplyEvent.getUserApply();
        Integer unreadCount = userApplyDao.getUnreadCount(userApply.getTargetId());
        //todo send websocket message in cluster
    }
}
