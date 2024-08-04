package com.youmin.imsystem.common.chat.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Getter
public enum HotFlagEnum {

    NOT(0, "Not Hot"),
    YES(1, "Hot"),
    ;

    private final Integer type;
    private final String desc;
}
