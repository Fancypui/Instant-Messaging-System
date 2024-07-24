package com.youmin.imsystem.common.user.enums;


import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum UserActiveStatusEnum {
    ONLINE(1,"online"),
    OFFLINE(2,"offline");

    private Integer type;
    private String desc;
}
