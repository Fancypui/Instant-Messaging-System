package com.youmin.imsystem.common.common.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MsgSendMessageDTO implements Serializable {
    private Long msgId;
}
