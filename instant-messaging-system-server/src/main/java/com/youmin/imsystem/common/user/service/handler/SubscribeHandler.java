package com.youmin.imsystem.common.user.service.handler;

import com.youmin.imsystem.common.user.service.WxMsgService;
import com.youmin.imsystem.common.user.service.adapter.TextBuilder;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.common.session.WxSessionManager;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Objects;

@Component
public class SubscribeHandler extends AbstractHandler{
    @Autowired
    @Lazy
    private WxMsgService wxMsgService;
    @Override
    public WxMpXmlOutMessage handle(WxMpXmlMessage wxMpXmlMessage, Map<String, Object> map, WxMpService wxMpService, WxSessionManager wxSessionManager) throws WxErrorException {
        this.logger.info("New user [OpenID: {}] subscribed our wechat official account",wxMpXmlMessage.getFromUser());
        WxMpXmlOutMessage responseResult = null;
        try{
            responseResult = wxMsgService.scan(wxMpXmlMessage);
        }catch (Exception e){
            this.logger.error(e.getMessage(),e);
        }
        if(responseResult==null){
            return TextBuilder.build("Thanks for subscribing",wxMpXmlMessage);
        }
        return responseResult;
    }
}
