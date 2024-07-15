package com.youmin.imsystem.common.user.service;

import me.chanjar.weixin.common.bean.WxOAuth2UserInfo;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;

public interface WxMsgService {
    WxMpXmlOutMessage scan(WxMpXmlMessage wxMsg);
    void authorized(WxOAuth2UserInfo userInfo);
}
