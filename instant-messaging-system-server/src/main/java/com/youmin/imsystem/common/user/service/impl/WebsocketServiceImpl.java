package com.youmin.imsystem.common.user.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.json.JSONUtil;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.youmin.imsystem.common.common.config.ThreadPoolConfig;
import com.youmin.imsystem.common.common.event.UserOfflineEvent;
import com.youmin.imsystem.common.common.event.UserOnlineEvent;
import com.youmin.imsystem.common.user.dao.UserDao;
import com.youmin.imsystem.common.user.domain.entity.User;
import com.youmin.imsystem.common.user.enums.RoleEnum;
import com.youmin.imsystem.common.user.service.IRoleService;
import com.youmin.imsystem.common.user.service.LoginService;
import com.youmin.imsystem.websocket.NettyUtils;
import com.youmin.imsystem.common.user.domain.dto.WSChannelExtraDTO;
import com.youmin.imsystem.common.user.domain.vo.resp.WSLoginSuccessResp;
import com.youmin.imsystem.common.user.domain.vo.resp.WSRespBase;
import com.youmin.imsystem.common.user.service.WebsocketService;
import com.youmin.imsystem.common.user.service.adapter.WSAdapter;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.result.WxMpQrCodeTicket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;


import java.time.Duration;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
@Slf4j
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

    /**
     * all online users and their corresponding channel
     */
    private static final ConcurrentHashMap<Long, CopyOnWriteArrayList<Channel>> ONLINE_UID_MAP = new ConcurrentHashMap<>();

    @Autowired
    private LoginService loginService;

    @Autowired
    private WxMpService wxMpService;

    @Autowired
    private UserDao userDao;

    @Autowired
    private IRoleService roleService;

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    @Autowired
    @Qualifier(ThreadPoolConfig.WEBSOCKET_EXECUTOR)
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;

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
        WSChannelExtraDTO wsChannelExtraDTO = ONLINE_WS_MAP.get(channel);
        Optional<Long> uidOptional = Optional.ofNullable(wsChannelExtraDTO).map(WSChannelExtraDTO::getUid);
        boolean offlineAll = offline(channel, uidOptional);
        if(uidOptional.isPresent() && offlineAll){
            User user = new User();
            user.setId(uidOptional.get());
            user.setLastOptTime(new Date());
            applicationEventPublisher.publishEvent(new UserOfflineEvent(this,user));
        }

    }


    /**
     * Remove channel from ONLINE_UID_MAP and ONLINE_WS_MAP
     * @param channel
     * @param uidOptional
     * @return
     */
    private boolean offline(Channel channel, Optional<Long> uidOptional){
        ONLINE_WS_MAP.remove(channel);
        if(uidOptional.isPresent()){
            CopyOnWriteArrayList<Channel> channels = ONLINE_UID_MAP.get(uidOptional.get());
            if(CollectionUtil.isNotEmpty(channels)){
                //remove channel, i use list so as to store mutiple channel of same user, where they login from multiple endpoint
                //such as laptop and phone
                channels.removeIf(a->Objects.equals(a,channels));
            }
            return CollectionUtil.isEmpty(ONLINE_UID_MAP.get(uidOptional.get()));
        }
        return true;
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
        String token = loginService.login(uid);
        loginSuccess(channel,user,token);
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

    public void sendToAll(WSRespBase<?> wsRespBase,Long skipUid){
        ONLINE_WS_MAP.forEach((channel, ext)->{
            if(Objects.nonNull(skipUid)&&Objects.equals(skipUid,ext.getUid())){
                return;
            }
            threadPoolTaskExecutor.execute(()->sendMsg(channel,wsRespBase));
        });
    }
    public void sendToAll(WSRespBase<?> wsRespBase){
        sendToAll(wsRespBase,null);
    }

    public void sendToUid(WSRespBase<?> wsRespBase, Long uid){
        CopyOnWriteArrayList<Channel> channels = ONLINE_UID_MAP.get(uid);
        if(CollectionUtil.isEmpty(channels)){
            log.info("User：{}is not online", uid);
            return;
        }
        channels.forEach(channel -> {
            threadPoolTaskExecutor.execute(()->sendMsg(channel,wsRespBase));
        });
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

    public void loginSuccess(Channel channel, User user, String token){
        online(channel, user.getId());
        boolean power = roleService.hasPower(user.getId(), RoleEnum.CHAT_MANAGER);
        WSRespBase<WSLoginSuccessResp> resp = WSAdapter.build(user, token,power);
        sendMsg(channel,resp);
        //update user latest online time
        user.setLastOptTime(new Date());
        //refresh user's latest ip, to get his/her location
        user.refreshIp(NettyUtils.getAttribute(channel,NettyUtils.IP));
        //user online event
        applicationEventPublisher.publishEvent(new UserOnlineEvent(this, user));
    }




    /**
     * user online
     * @param channel
     * @param uid
     */
    private void online(Channel channel,Long uid){
        getOrInitChannelExtra(channel).setUid(uid);//initialize channelExtraDTO with uid
        ONLINE_UID_MAP.putIfAbsent(uid,new CopyOnWriteArrayList<>());//to adapt to scenario where user login from multiple endpoint
        ONLINE_UID_MAP.get(uid).add(channel);
        NettyUtils.setAttribute(channel, NettyUtils.UID,uid);
    }

    /**
     * put ChannelExtra if absent
     * @param channel
     * @return
     */
    private WSChannelExtraDTO getOrInitChannelExtra(Channel channel){
        WSChannelExtraDTO wsChannelExtraDTO = ONLINE_WS_MAP.getOrDefault(channel, new WSChannelExtraDTO());
        WSChannelExtraDTO old = ONLINE_WS_MAP.putIfAbsent(channel, wsChannelExtraDTO);
        return Objects.isNull(old)?wsChannelExtraDTO:old;
    }




    public void authorize(Channel channel,String token){
        Long validUid = loginService.getValidUid(token);
        if(Objects.nonNull(validUid)){//user authentication passed, let user login
            User user = this.userDao.getById(validUid);
            loginSuccess(channel,user,token);
        }else{//let client-side token invalida
            sendMsg(channel,WSAdapter.buildInvalidateResp());
        }
    }
}
