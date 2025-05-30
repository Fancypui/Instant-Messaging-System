package com.youmin.imsystem.common.common.exception;

import lombok.Data;

@Data
public class BusinessException extends RuntimeException{

    private Integer errorCode;
    private String errorMsg;

    public BusinessException(String errorMsg){
        super(errorMsg);
        this.errorCode = CommonErrorEnum.BUSINESS_ERROR.getErrorCode();
        this.errorMsg = errorMsg;
    }

    public BusinessException(Integer errorCode, String errorMsg){
        super(errorMsg);
        this.errorMsg = errorMsg;
        this.errorCode = errorCode;
    }
    public BusinessException(ErrorEnum errorEnum){
        super(errorEnum.getErrorMessage());
        this.errorMsg = errorEnum.getErrorMessage();
        this.errorCode = errorEnum.getErrorCode();
    }
}
