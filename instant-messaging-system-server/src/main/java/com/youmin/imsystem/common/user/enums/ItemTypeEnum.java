package com.youmin.imsystem.common.user.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ItemTypeEnum {

    MODIFY_NAME_CARD(1,"MODIFY NAME CARD"),
    BADGES(2,"BADGES");

    private final Integer type;
    private final String desc;


}
