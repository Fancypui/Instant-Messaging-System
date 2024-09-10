package com.youmin.imsystem.common.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum GroupErrorEnum implements ErrorEnum {
    /**
     *
     */
    GROUP_NOT_EXIST(9001, "Group Not Exist"),
    NOT_ALLOWED_OPERATION(9002, "NOT_ALLOWED_OPERATION~"),
    MANAGE_COUNT_EXCEED(9003, "maximum group member reach~"),
    USER_NOT_IN_GROUP(9004, "user not in the group~"),
    NOT_ALLOWED_FOR_REMOVE(9005, "you are not allowed to remove user"),
    NOT_ALLOWED_FOR_EXIT_GROUP(9006, "Big group not allow for exit"),
    ;
    private final Integer code;
    private final String msg;



    @Override
    public Integer getErrorCode() {
        return this.code;
    }

    @Override
    public String getErrorMessage() {
        return this.msg;
    }
}
