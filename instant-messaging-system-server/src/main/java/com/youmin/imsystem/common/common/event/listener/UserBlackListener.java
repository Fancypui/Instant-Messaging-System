package com.youmin.imsystem.common.common.event.listener;

import com.youmin.imsystem.common.common.domain.enums.YesOrNoEnum;
import com.youmin.imsystem.common.common.event.UserBlackEvent;
import com.youmin.imsystem.common.common.event.UserOnlineEvent;
import com.youmin.imsystem.common.user.cache.UserCache;
import com.youmin.imsystem.common.user.dao.UserDao;
import com.youmin.imsystem.common.user.domain.entity.User;
import com.youmin.imsystem.common.user.enums.UserActiveStatusEnum;
import com.youmin.imsystem.common.user.service.IpService;
import com.youmin.imsystem.websocket.domain.enums.WSRespTypeEnum;
import com.youmin.imsystem.websocket.domain.vo.resp.WSBlack;
import com.youmin.imsystem.websocket.domain.vo.resp.WSRespBase;
import com.youmin.imsystem.websocket.service.WebsocketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class UserBlackListener {

    @Autowired
    private UserDao userDao;

    @Autowired
    private UserCache userCache;

    @Autowired
    private IpService ipService;

    @Autowired
    private WebsocketService websocketService;

    @Async
    @TransactionalEventListener(value = UserBlackEvent.class, phase = TransactionPhase.AFTER_COMMIT)
    public void sendMsg(UserBlackEvent userBlackEvent){
        User user = userBlackEvent.getUser();
        WSRespBase<WSBlack> wsRespBase = new WSRespBase<>();
        wsRespBase.setType(WSRespTypeEnum.BLACK.getType());
        WSBlack wsBlack = new WSBlack();
        wsBlack.setUid(user.getId());
        wsRespBase.setData(wsBlack);
        websocketService.sendToAll(wsRespBase);
    }
    @Async
    @TransactionalEventListener(value = UserBlackEvent.class, phase = TransactionPhase.AFTER_COMMIT)
    public void changeUserStatus(UserBlackEvent userBlackEvent){
        userDao.invalidUser(userBlackEvent.getUser().getId());

    }

    @Async
    @TransactionalEventListener(value = UserBlackEvent.class, phase = TransactionPhase.AFTER_COMMIT)
    public void removeBlackCache(UserBlackEvent userBlackEvent){
        userCache.evictBlackMap();

    }
}
