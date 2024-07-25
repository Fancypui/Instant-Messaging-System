package com.youmin.imsystem.websocket.service;


import com.youmin.imsystem.common.user.domain.entity.User;
import com.youmin.imsystem.websocket.domain.vo.resp.WSRespBase;
import io.netty.channel.Channel;

public interface WebsocketService {

    void connect(Channel channel);
    void handleLoginReq(Channel channel);

    void remove(Channel channel);
    void scanLoginSuccess(Integer code, Long uid);
    void waitAuthorized(Integer code);
    void sendToAll(WSRespBase<?> wsRespBase);
}
