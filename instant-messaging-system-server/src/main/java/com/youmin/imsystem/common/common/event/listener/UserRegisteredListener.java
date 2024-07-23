package com.youmin.imsystem.common.common.event.listener;

import com.youmin.imsystem.common.common.domain.enums.IdempotentEnum;
import com.youmin.imsystem.common.common.event.UserRegisteredEvent;
import com.youmin.imsystem.common.user.dao.UserDao;
import com.youmin.imsystem.common.user.domain.entity.User;
import com.youmin.imsystem.common.user.enums.ItemEnum;
import com.youmin.imsystem.common.user.service.IUserBackpackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class UserRegisteredListener {

    @Autowired
    private IUserBackpackService userBackpackService;

    @Autowired
    private UserDao userDao;

    @Async
    @TransactionalEventListener(classes = UserRegisteredEvent.class, phase = TransactionPhase.AFTER_COMMIT)
    public void issueModifyNameCard(UserRegisteredEvent userRegisteredEvent){
        User user = userRegisteredEvent.getUser();
        userBackpackService.acquireItem(user.getId(), ItemEnum.MODIFY_NAME_CARD.getItemId(), IdempotentEnum.UID,user.getId()+"");
    }

    @Async
    @TransactionalEventListener(classes = UserRegisteredEvent.class, phase = TransactionPhase.AFTER_COMMIT)
    public void issueBadges(UserRegisteredEvent userRegisteredEvent){
        User user = userRegisteredEvent.getUser();
        int registeredUserCount = userDao.count();
        if(registeredUserCount<10){
            userBackpackService.acquireItem(user.getId(), ItemEnum.REG_TOP10_BADGE.getItemId(),  IdempotentEnum.UID,user.getId()+"");
        }else if(registeredUserCount<100){
            userBackpackService.acquireItem(user.getId(), ItemEnum.REG_TOP10_BADGE.getItemId(),  IdempotentEnum.UID,user.getId()+"");
        }

    }

}
