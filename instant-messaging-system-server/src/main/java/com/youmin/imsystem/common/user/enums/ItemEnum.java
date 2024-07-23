package com.youmin.imsystem.common.user.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ItemEnum {
    MODIFY_NAME_CARD(1L,1,"Modify name card"),
    REG_TOP10_BADGE(3L, 1, "First 10 registered user"),
    REG_TOP100_BADGE(4L, 1, "前100注册徽章");
    private long itemId;
    private int itemTypeId;
    private String desc;

}
