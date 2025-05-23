package com.youmin.imsystem.common.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum CommonErrorEnum implements ErrorEnum {

    SYSTEM_ERROR(-1, "System Error"),
    PARAM_VALID(-2, "Parameter Validation Fail"),
    BUSINESS_ERROR(0,"{0}"),
    LOCK_LIMIT(-3,"LOCK LIMIT")
    ;
    private final Integer code;
    private final String msg;


    @Override
    public String getErrorMessage() {
        return this.msg;
    }

    @Override
    public Integer getErrorCode() {
        return this.code;
    }
}