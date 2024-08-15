package com.youmin.imsystem.common.user.domain.vo.resp;

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
    //todo user permission 0 normal chat member, 1 chat manager
    private Integer power;
}
