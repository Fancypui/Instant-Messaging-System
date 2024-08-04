package com.youmin.imsystem.common.user.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UserApplyReadStatusEnum {
    READ(1,"Have Been Read"),
    NOT_YET_READ(2,"No yet been read");

    private final Integer statusCode;
    private final String desc;
}
