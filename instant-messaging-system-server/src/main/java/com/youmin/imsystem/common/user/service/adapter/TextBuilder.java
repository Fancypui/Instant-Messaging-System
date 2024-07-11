package com.youmin.imsystem.common.user.service.adapter;

import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;

public class TextBuilder {
    public static WxMpXmlOutMessage build(String content, WxMpXmlMessage wpMpXmlMessage){
            return WxMpXmlOutMessage.TEXT().content(content).
                fromUser(wpMpXmlMessage.getToUser()).
                toUser(wpMpXmlMessage.getFromUser()).build();
    }
}
