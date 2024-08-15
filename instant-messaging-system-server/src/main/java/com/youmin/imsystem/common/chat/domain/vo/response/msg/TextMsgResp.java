package com.youmin.imsystem.common.chat.domain.vo.response.msg;

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
public class TextMsgResp {

    @ApiModelProperty("message content")
    private String content;

    @ApiModelProperty("tagged uid")
    private List<Long> atUidList;

    @ApiModelProperty("parent msg,if no parent msg then it will be null")
    private TextMsgResp.ReplyMsg reply;

    //todo url mapping


    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ReplyMsg{
        @ApiModelProperty("message id")
        private Long id;

        @ApiModelProperty("user id")
        private Long uid;

        @ApiModelProperty("user name")
        private String username;

        @ApiModelProperty("message type 1normal text 2 recall msg and more")
        private Integer type;

        @ApiModelProperty("Message content, different message type having different body")
        private Object body;

        @ApiModelProperty("whether can callback 0 no 1 yes")
        private Integer canCallBack;

        @ApiModelProperty("distance between reply message and parent msg")
        private Integer gapCount;



    }
}
