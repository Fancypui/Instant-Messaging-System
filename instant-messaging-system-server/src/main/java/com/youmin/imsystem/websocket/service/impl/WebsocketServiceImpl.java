package com.youmin.imsystem.websocket.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.youmin.imsystem.common.user.dao.UserDao;
import com.youmin.imsystem.common.user.domain.entity.User;
import com.youmin.imsystem.common.user.service.LoginService;
import com.youmin.imsystem.websocket.domain.dto.WSChannelExtraDTO;
import com.youmin.imsystem.websocket.domain.vo.resp.WSLoginSuccessResp;
import com.youmin.imsystem.websocket.domain.vo.resp.WSRespBase;
import com.youmin.imsystem.websocket.service.WebsocketService;
import com.youmin.imsystem.websocket.service.adapter.WSAdapter;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.SneakyThrows;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.result.WxMpQrCodeTicket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;


import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
@Service
public class WebsocketServiceImpl implements WebsocketService {

    private static final Duration EXPIRE_TIME = Duration.ofHours(1);
    private static final Long QR_CODE_EXPIRE_TIME = 100000L;

    /**
     * All websockets that have been established connection with backend and its relationship with users' uid
     */
    private static final ConcurrentHashMap<Channel, WSChannelExtraDTO> ONLINE_WS_MAP
            = new ConcurrentHashMap<>();

    /**
     * build relationship between login code with channel
     */
    private static final Cache<Integer, Channel> WS_LOGIN_MAP = Caffeine.newBuilder()
            .expireAfterAccess(EXPIRE_TIME).build();

    @Autowired
    private LoginService loginService;

    @Autowired
    private WxMpService wxMpService;

    @Autowired
    private UserDao userDao;

    public void connect(Channel channel){
        ONLINE_WS_MAP.put(channel, new WSChannelExtraDTO());
    }

    /**
     * handle login request from user, return a QR code that contains code
     * @param channel
     */
    @SneakyThrows
    public void handleLoginReq(Channel channel){
        //generate unique login code
        Integer code = generateCode(channel);
        //request wechat api to get login url
        WxMpQrCodeTicket wxMpQrCodeTicket = wxMpService.getQrcodeService().qrCodeCreateTmpTicket(code, (int) EXPIRE_TIME.getSeconds());
        sendMsg(channel, WSAdapter.build(wxMpQrCodeTicket));
    }

    /**
     * user offline/ connection break, remove user's channel from online_ws_map
     * @param channel
     */
    @Override
    public void remove(Channel channel) {
        ONLINE_WS_MAP.remove(channel);
    }

    /**
     * If user has been successfully registered and authorized, this method will be called
     * @param code
     * @param uid
     */
    @Override
    public void scanLoginSuccess(Integer code, Long uid) {
        //to ensure the channel is in this host
        Channel channel = WS_LOGIN_MAP.getIfPresent(code);
        if(Objects.isNull(channel)){
            return;
        }
        User user = userDao.getById(uid);
        //remove code
        WS_LOGIN_MAP.invalidate(code);
        //call login module to get token
        String token = loginService.login();
        WSRespBase<WSLoginSuccessResp> loginSuccessResp = WSAdapter.build(user, token);
        sendMsg(channel,loginSuccessResp);
    }

    /**
     * Inform client-side that currently is in waiting authorization stage
     * @param code
     */
    @Override
    public void waitAuthorized(Integer code) {
        Channel channel = WS_LOGIN_MAP.getIfPresent(code);
        if(Objects.isNull(channel)){
            return;
        }

        sendMsg(channel, WSAdapter.buildWaitAuthorized());
    }

    /**
     * send response back to client-side
     * @param channel
     * @param wsRespBase
     */
    public void sendMsg(Channel channel, WSRespBase<?> wsRespBase){
        channel.writeAndFlush(new TextWebSocketFrame(JSONUtil.toJsonStr(wsRespBase)));
    }

    /**
     * generate unique login code
     * @param channel
     * @return
     */
    private Integer generateCode(Channel channel){
        int code;
        do{
            code = RandomUtil.randomInt(Integer.MAX_VALUE);
        }while (Objects.nonNull(WS_LOGIN_MAP.asMap().put(code,channel)));
        return code;
    }
}
