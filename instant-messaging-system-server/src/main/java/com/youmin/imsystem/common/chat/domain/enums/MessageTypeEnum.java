package com.youmin.imsystem.common.chat.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum MessageTypeEnum {

    TEXT(1,"text message"),
    RECALL(2,"Recall message"),
    IMG(3,"image"),
    FILE(4,"File based"),
    SOUND(5,"sound"),
    VIDEO(6,"video"),
    EMOJI(7,"emoji"),
    SYSTEM(8,"system");

    private final Integer msgType;

    private final String desc;
}
