package com.youmin.imsystem.common.user.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ItemEnum {
    MODIFY_NAME_CARD(1L,1,"Modify name card");

    private long itemId;
    private int itemTypeId;
    private String desc;

}
