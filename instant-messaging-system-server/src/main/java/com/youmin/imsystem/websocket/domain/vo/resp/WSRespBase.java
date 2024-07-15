package com.youmin.imsystem.websocket.domain.vo.resp;

import com.youmin.imsystem.websocket.domain.enums.WSRespTypeEnum;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Websockat Response (Base class)
 * @param <T>
 */
@Data
@NoArgsConstructor
public class WSRespBase<T> {
    /**
     * @see WSRespTypeEnum
     */
    private Integer type;
    private T data;
}
