package com.youmin.imsystem.common.user.service.handler;

import com.youmin.imsystem.common.user.service.adapter.TextBuilder;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.common.session.WxSessionManager;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import org.springframework.stereotype.Component;

import java.util.Map;
@Component
public class SubscribeHandler extends AbstractHandler{
    @Override
    public WxMpXmlOutMessage handle(WxMpXmlMessage wxMpXmlMessage, Map<String, Object> map, WxMpService wxMpService, WxSessionManager wxSessionManager) throws WxErrorException {
        String fromUser = wxMpXmlMessage.getFromUser();
        String eventKey = wxMpXmlMessage.getEventKey();
        this.logger.info("User Scan Event From Wechat, Scene ID:{}, User's Openid",eventKey,fromUser);
        return TextBuilder.build("Thanks for subscribe",wxMpXmlMessage);
    }
}
