package com.youmin.imsystem.common.user.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum BlackTypeEnum {

    UID(1,"UID"),
    IP(2,"IP");

    private Integer type;
    private String desc;
}
