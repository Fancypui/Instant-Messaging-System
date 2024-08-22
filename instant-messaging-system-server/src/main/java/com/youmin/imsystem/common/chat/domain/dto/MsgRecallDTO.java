package com.youmin.imsystem.common.chat.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MsgRecallDTO {

    private Long roomId;

    private Long msgId;

    //person who recall the message
    private Long recallUid;

}
