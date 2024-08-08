package com.youmin.imsystem.common.chat.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum MessageStatusEnum {

    NORMAL(0, "Normal"),
    DELETE(1, "Deleted"),
    ;

    private final Integer status;
    private final String desc;
}
