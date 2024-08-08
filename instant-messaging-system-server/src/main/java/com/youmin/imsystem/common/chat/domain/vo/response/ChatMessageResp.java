package com.youmin.imsystem.common.chat.domain.vo.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessageResp {


    @ApiModelProperty("sender info")
    private UserInfo fromUser;

    @ApiModelProperty("message detail")
    private Message message;



    @Data
    public static class UserInfo{
        @ApiModelProperty("user id")
        private Long uid;
    }

    @Data
    public static class Message{

        @ApiModelProperty("message id")
        private Long id;

        @ApiModelProperty("room id")
        private Long roomId;

        @ApiModelProperty("send time")
        private Date sendTime;

        @ApiModelProperty("message type")
        private Integer msgType;

        @ApiModelProperty("Different message type has different body content")
        private Object body;

        @ApiModelProperty("message mark")
        private MessageMark messageMark;

    }

    @Data
    public static class MessageMark{

        @ApiModelProperty("message like count")
        private Integer likeCount;

        @ApiModelProperty("if current user like 0 false 1 yes")
        private Integer userLike;

        @ApiModelProperty("if current user dislike 0false, 1 yes")
        private Integer disLike;
        @ApiModelProperty("message dislike count")
        private Integer dislikeCount;
    }
}
