package com.youmin.imsystem.common.user.domain.vo.resp;

import com.youmin.imsystem.common.user.enums.WSRespTypeEnum;
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
