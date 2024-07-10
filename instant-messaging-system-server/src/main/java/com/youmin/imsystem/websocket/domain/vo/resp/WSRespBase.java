package com.youmin.imsystem.websocket.domain.vo.resp;

import com.youmin.imsystem.websocket.domain.enums.WSRespTypeEnum;

public class WSRespBase<T> {
    /**
     * @see WSRespTypeEnum
     */
    private Integer type;
    private T data;
}
