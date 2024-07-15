package com.youmin.imsystem.websocket.domain.vo.resp;

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
