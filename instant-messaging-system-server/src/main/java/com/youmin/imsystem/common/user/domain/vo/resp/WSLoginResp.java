package com.youmin.imsystem.common.user.domain.vo.resp;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Websocket login resp
 */
@Data
@AllArgsConstructor
public class WSLoginResp {
    //login url requested from wechat
    private String url;
}
