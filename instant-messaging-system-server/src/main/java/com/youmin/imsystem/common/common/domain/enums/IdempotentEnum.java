package com.youmin.imsystem.common.common.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum IdempotentEnum {

    UID(1,"UID"),
    MSG_ID(2,"Message ID");

    private final Integer type;
    private final String desc;
}
