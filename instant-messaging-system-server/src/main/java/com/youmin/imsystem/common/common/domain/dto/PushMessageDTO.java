package com.youmin.imsystem.common.common.domain.dto;

import com.youmin.imsystem.common.user.domain.vo.req.WSReqBase;
import com.youmin.imsystem.common.user.domain.vo.resp.WSRespBase;
import com.youmin.imsystem.common.user.enums.WSPushTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PushMessageDTO {
    /**
     * push message's data
     */
    private WSRespBase<?> wsRespBase;
    /**
     * user to push to
     */
    private List<Long> uidList;

    /**
     * push type 1 individual 2 All members
     */
    private Integer pushType;

    public PushMessageDTO(WSRespBase<?> wsRespBase){
        this.wsRespBase = wsRespBase;
        this.pushType = WSPushTypeEnum.ALL.getType();
    }
    public PushMessageDTO(WSRespBase<?> wsRespBase,List<Long> uidList){
        this.wsRespBase = wsRespBase;
        this.uidList = uidList;
        this.pushType = WSPushTypeEnum.USER.getType();
    }
}
