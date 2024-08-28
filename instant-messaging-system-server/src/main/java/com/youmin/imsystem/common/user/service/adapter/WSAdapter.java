package com.youmin.imsystem.common.user.service.adapter;

import cn.hutool.core.bean.BeanUtil;
import com.youmin.imsystem.common.chat.domain.dto.ChatMessageMarkDTO;
import com.youmin.imsystem.common.chat.domain.dto.MsgRecallDTO;
import com.youmin.imsystem.common.chat.domain.vo.response.ChatMessageResp;
import com.youmin.imsystem.common.common.domain.enums.YesOrNoEnum;
import com.youmin.imsystem.common.user.domain.entity.User;
import com.youmin.imsystem.common.user.domain.vo.resp.*;
import com.youmin.imsystem.common.user.enums.WSRespTypeEnum;
import me.chanjar.weixin.mp.bean.result.WxMpQrCodeTicket;

import java.util.Collections;

public class WSAdapter {

    public static WSRespBase<WSLoginResp> build(WxMpQrCodeTicket wxMpQrCodeTicket){
        WSRespBase<WSLoginResp> wsRespBase = new WSRespBase<>();
        wsRespBase.setType(WSRespTypeEnum.LOGIN_URL.getType());
        wsRespBase.setData(new WSLoginResp(wxMpQrCodeTicket.getUrl()));
        return wsRespBase;
    }
    public static WSRespBase<WSLoginSuccessResp> build(User user,String token, boolean power){
        WSRespBase<WSLoginSuccessResp> wsRespBase = new WSRespBase<>();
        WSLoginSuccessResp build = WSLoginSuccessResp.builder()
                .uid(user.getId())
                .avatar(user.getAvatar())
                .name(user.getName())
                .power(power? YesOrNoEnum.YES.getStatus() : YesOrNoEnum.NO.getStatus())
                .token(token)
                .build();
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

    public static WSRespBase<?> buildMsgSend(ChatMessageResp msgResp) {
        WSRespBase<ChatMessageResp> wsBaseResp = new WSRespBase<>();
        wsBaseResp.setData(msgResp);
        wsBaseResp.setType(WSRespTypeEnum.MESSAGE.getType());
        return wsBaseResp;
    }

    public static WSRespBase<WSMsgRecall> buildMsgRecall(MsgRecallDTO msgRecallDTO) {
        WSRespBase<WSMsgRecall> resp = new WSRespBase<>();
        WSMsgRecall wsMsgRecall = new WSMsgRecall();
        BeanUtil.copyProperties(msgRecallDTO,wsMsgRecall);
        resp.setData(wsMsgRecall);
        resp.setType(WSRespTypeEnum.RECALL.getType());
        return resp;
    }

    public static WSRespBase<WSMsgMark> buildMsgMarkSend(ChatMessageMarkDTO dto, Integer markCount) {
        WSMsgMark.WSMsgMarkItem item = new WSMsgMark.WSMsgMarkItem();
        BeanUtil.copyProperties(dto,item);
        item.setMarkCount(markCount);
        WSRespBase<WSMsgMark> wsRespBase = new WSRespBase<>();
        wsRespBase.setType(WSRespTypeEnum.MARK.getType());
        WSMsgMark wsMsgMark = new WSMsgMark();
        wsMsgMark.setMarkList(Collections.singletonList(item));
        wsRespBase.setData(wsMsgMark);
        return wsRespBase;
    }
}
