package com.youmin.imsystem.websocket.service.adapter;

import com.youmin.imsystem.common.user.domain.entity.User;
import com.youmin.imsystem.websocket.domain.enums.WSRespTypeEnum;
import com.youmin.imsystem.websocket.domain.vo.resp.WSLoginResp;
import com.youmin.imsystem.websocket.domain.vo.resp.WSLoginSuccessResp;
import com.youmin.imsystem.websocket.domain.vo.resp.WSRespBase;
import me.chanjar.weixin.mp.bean.result.WxMpQrCodeTicket;

public class WSAdapter {

    public static WSRespBase<WSLoginResp> build(WxMpQrCodeTicket wxMpQrCodeTicket){
        WSRespBase<WSLoginResp> wsRespBase = new WSRespBase<>();
        wsRespBase.setType(WSRespTypeEnum.LOGIN_URL.getType());
        wsRespBase.setData(new WSLoginResp(wxMpQrCodeTicket.getUrl()));
        return wsRespBase;
    }
    public static WSRespBase<WSLoginSuccessResp> build(User user,String token){
        WSRespBase<WSLoginSuccessResp> wsRespBase = new WSRespBase<>();
        WSLoginSuccessResp build = WSLoginSuccessResp.builder()
                .uid(user.getId())
                .avatar(user.getAvatar())
                .name(user.getName())
                .token(token).build();
        wsRespBase.setType(WSRespTypeEnum.LOGIN_SUCcESS.getType());
        wsRespBase.setData(build);
        return wsRespBase;
    }

    public static WSRespBase<?> buildWaitAuthorized(){
        WSRespBase<WSLoginResp> wsRespBase = new WSRespBase<>();
        wsRespBase.setType(WSRespTypeEnum.LOGIN_SCAN_SUCCESS.getType());
        return wsRespBase;
    }

    public static WSRespBase<?> buildInvalidateResp(){
        WSRespBase<WSLoginResp> wsRespBase = new WSRespBase<>();
        wsRespBase.setType(WSRespTypeEnum.INVALIDATE_TOKEN.getType());
        return wsRespBase;
    }
}
