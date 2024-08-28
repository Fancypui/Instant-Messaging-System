package com.youmin.imsystem.common.user.domain.vo.resp;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WSMsgMark {

    private List<WSMsgMarkItem> markList;
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class WSMsgMarkItem{
        @ApiModelProperty("uid")
        private Long uid;
        @ApiModelProperty("msgId")
        private Long msgId;
        @ApiModelProperty("1 Like 2 dislike")
        private Integer markType;
        @ApiModelProperty("mark count")
        private Integer markCount;
        @ApiModelProperty("1Confirm 2Cancel")
        private Integer actType;
    }
}
