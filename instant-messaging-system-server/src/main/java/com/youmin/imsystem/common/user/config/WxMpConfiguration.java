package com.youmin.imsystem.common.user.config;

import com.youmin.imsystem.common.user.service.handler.LogHandler;
import com.youmin.imsystem.common.user.service.handler.MsgHandler;
import com.youmin.imsystem.common.user.service.handler.ScanHandler;
import com.youmin.imsystem.common.user.service.handler.SubscribeHandler;
import lombok.AllArgsConstructor;
import me.chanjar.weixin.common.api.WxConsts;
import me.chanjar.weixin.mp.api.WxMpMessageRouter;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.api.impl.WxMpServiceImpl;
import me.chanjar.weixin.mp.config.impl.WxMpDefaultConfigImpl;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.stream.Collectors;

import static me.chanjar.weixin.common.api.WxConsts.EventType.SUBSCRIBE;
import static me.chanjar.weixin.common.api.WxConsts.XmlMsgType.EVENT;

@AllArgsConstructor
@Configuration
@EnableConfigurationProperties(WxMpProperties.class)
public class WxMpConfiguration {
    private final LogHandler logHandler;
    private final MsgHandler msgHandler;
    private final SubscribeHandler subscribeHandler;
    private final ScanHandler scanHandler;
    private final WxMpProperties wxMpProperties;

    @Bean
    public WxMpService wxMpService(){
        final List<WxMpProperties.MpConfig> config = this.wxMpProperties.getConfigs();
        WxMpService service = new WxMpServiceImpl();
        service.setMultiConfigStorages(config
                .stream().map(a->{
                    WxMpDefaultConfigImpl configStorage = new WxMpDefaultConfigImpl();
                    configStorage.setAppId(a.getAppId());
                    configStorage.setSecret(a.getSecret());
                    configStorage.setToken(a.getToken());
                    configStorage.setAesKey(a.getAesKey());
                    return configStorage;
                }).collect(Collectors.toMap(WxMpDefaultConfigImpl::getAppId,a->a,(o,n)->n)));
        return service;
    }

    @Bean
    public WxMpMessageRouter messageRouter(WxMpService wxMpService){
        WxMpMessageRouter wxMpMessageRouter = new WxMpMessageRouter(wxMpService);
        //record every event (async)
        wxMpMessageRouter.rule().handler(this.logHandler).next();
        //subscribe event
        wxMpMessageRouter.rule().async(false).msgType(EVENT).event(SUBSCRIBE).handler(this.subscribeHandler).end();
        //qr code scan event
        wxMpMessageRouter.rule().async(false).msgType(EVENT).event(WxConsts.EventType.SCAN).handler(this.scanHandler).end();
        //default message
        wxMpMessageRouter.rule().async(false).handler(this.msgHandler).end();
        return wxMpMessageRouter;
    }
}
