package com.youmin.imsystem.common.user.domain.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Size;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SummaryUserInfoReq {
    @Size(max = 50)
    @ApiModelProperty("List of user info that wanted their info to be loaded")
    private List<SummaryUserInfoReq.UserInfoReq> userInfoReqList;


    @Data
    public static class UserInfoReq{
        @ApiModelProperty("uid")
        private Long uid;
        @ApiModelProperty("user's latest update time")
        private Long lastModifyTime;
    }
}
