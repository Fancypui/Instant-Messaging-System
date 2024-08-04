package com.youmin.imsystem.common.user.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum UserApplyStatusEnum {

    WAIT_APPROVING(1,"Pending request"),
    ACCEPT(2,"Friend Request Already Approve");

    private final Integer statusCode;
    private final String desc;
}
