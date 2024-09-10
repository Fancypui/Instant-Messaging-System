package com.youmin.imsystem.common.chat.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@AllArgsConstructor
@Getter
public enum GroupRoleAppEnum {
    LEADER(1, "group owner"),
    MANAGER(2, "group admin"),
    MEMBER(3, "member"),
    REMOVE(4, "member ady remove"),
    ;

    private final Integer type;
    private final String desc;

    private static Map<Integer, GroupRoleAppEnum> cache;

    static {
        cache = Arrays.stream(GroupRoleAppEnum.values()).collect(Collectors.toMap(GroupRoleAppEnum::getType, Function.identity()));
    }

    public static GroupRoleAppEnum of(Integer type) {
        return cache.get(type);
    }
}
