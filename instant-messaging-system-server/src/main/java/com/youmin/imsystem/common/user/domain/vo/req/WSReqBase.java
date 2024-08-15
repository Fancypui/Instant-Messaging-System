package com.youmin.imsystem.common.user.domain.vo.req;

import com.youmin.imsystem.common.user.enums.WSRespTypeEnum;
import lombok.Data;

@Data
public class WSReqBase {
    /**
     * @see WSRespTypeEnum
     */
    private Integer type;
    private String data;
}
