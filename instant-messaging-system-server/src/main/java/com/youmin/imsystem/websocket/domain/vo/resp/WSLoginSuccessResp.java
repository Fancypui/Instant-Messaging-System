package com.youmin.imsystem.websocket.domain.vo.resp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WSLoginSuccessResp {
    private Long uid;
    private String avatar;
    private String token;
    private String name;
    //todo user permission
    private Integer power;
}
