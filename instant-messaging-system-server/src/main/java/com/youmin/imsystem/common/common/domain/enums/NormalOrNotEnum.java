package com.youmin.imsystem.common.common.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;


@AllArgsConstructor
@Getter
public enum NormalOrNotEnum {

    NORMAL(0, "Normal"),
    NOT_NORMAL(1, "Abnormal"),
    ;

    private final Integer status;
    private final String desc;
}
