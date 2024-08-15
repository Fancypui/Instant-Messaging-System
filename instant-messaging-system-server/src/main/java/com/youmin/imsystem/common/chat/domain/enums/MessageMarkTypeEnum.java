package com.youmin.imsystem.common.chat.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum MessageMarkTypeEnum {
    LIKE(1, "Like", 10),
    DISLIKE(2, "Dislike", 5),
    ;

    private final Integer type;
    private final String desc;
    private final Integer riseNum;


}
