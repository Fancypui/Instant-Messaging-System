package com.youmin.imsystem.common.user.service;


import com.youmin.imsystem.common.user.domain.vo.resp.WSRespBase;
import io.netty.channel.Channel;

public interface WebsocketService {

    void connect(Channel channel);
    void handleLoginReq(Channel channel);

    void remove(Channel channel);
    void scanLoginSuccess(Integer code, Long uid);
    void waitAuthorized(Integer code);
    void sendToAll(WSRespBase<?> wsRespBase);
    void sendToUid(WSRespBase<?> wsRespBase,Long uid);
}
