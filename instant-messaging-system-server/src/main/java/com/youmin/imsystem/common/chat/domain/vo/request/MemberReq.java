package com.youmin.imsystem.common.chat.domain.vo.request;

import com.youmin.imsystem.common.common.domain.vo.req.CursorBaseReq;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemberReq extends CursorBaseReq {

    @ApiModelProperty("roomd if")
    private Long roomId = 1L;
}
