package com.youmin.imsystem.common.chat.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@AllArgsConstructor
@Getter
public enum GroupRoleEnum {
    /**
     *
     */
    LEADER(1, "group onwer"),
    MANAGER(2, "group admin"),
    MEMBER(3, "group member"),
    ;

    private final Integer type;
    private final String desc;

    public static final List<Integer> ADMIN_LIST = Arrays.asList(GroupRoleEnum.LEADER.getType(), GroupRoleEnum.MANAGER.getType());

    private static Map<Integer, GroupRoleEnum> cache;

    static {
        cache = Arrays.stream(GroupRoleEnum.values()).collect(Collectors.toMap(GroupRoleEnum::getType, Function.identity()));
    }

    public static GroupRoleEnum of(Integer type) {
        return cache.get(type);
    }
}
