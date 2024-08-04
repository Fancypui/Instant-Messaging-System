package com.youmin.imsystem.common.user.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UserApplyTypeEnum {
    ADD_FRIEND(1,"Add as friend");

    private final Integer type;
    private final String desc;
}
