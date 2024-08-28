package com.youmin.imsystem.common.user.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
public enum SceneEnum {
    CHAT(1, "chat", "/chat"),
    EMOJI(2, "emoji", "/emoji"),
    ;


    private final Integer type;
    private final String desc;
    private final String path;

    private static final Map<Integer, SceneEnum> cache;

    static {
        cache = Arrays.stream(SceneEnum.values()).collect(Collectors.toMap(SceneEnum::getType, Function.identity()));
    }

    public static SceneEnum of(Integer type) {
        return cache.get(type);
    }
}
