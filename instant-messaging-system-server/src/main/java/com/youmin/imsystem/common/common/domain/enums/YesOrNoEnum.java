package com.youmin.imsystem.common.common.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum YesOrNoEnum {
    NO(0, "NO"),
    YES(1, "YES"),
    ;

    private final Integer status;
    private final String desc;

    public static Integer toStatus(Boolean bool){
        return bool? YES.getStatus() : NO.getStatus();
    }
}
