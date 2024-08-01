package com.youmin.imsystem.common.user.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ChatActiveStatusEnum {
    ONLINE(1,"Online"),
    OFFLINE(2,"Offline");

    private final Integer status;
    private final String desc;

}
