package com.youmin.imsystem.common.user.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum RoleEnum {
    ADMIN(1L,"ADMIN"),
    CHAT_MANAGER(2L,"MODIFY NAME CARD");


    private final Long type;
    private final String desc;
}
