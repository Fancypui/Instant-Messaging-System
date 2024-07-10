package com.youmin.imsystem.websocket.domain.vo.req;

import com.youmin.imsystem.websocket.domain.enums.WSRespTypeEnum;
import lombok.Data;

@Data
public class WSReqBase {
    /**
     * @see WSRespTypeEnum
     */
    private Integer type;
    private String data;
}
