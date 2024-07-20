package com.youmin.imsystem.common.common.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Http Error
 */
@AllArgsConstructor
@Getter
public enum HttpErrorEnum implements ErrorEnum{
    /**
     * Token Verification fail
     */
    ACCESS_DENIED(401,"LOGIN FAIL, PLEASE RETRY LOGIN");

    private int httpCode;
    /**
     * Fail message
     */
    private String msg;


    @Override
    public String getErrorMessage() {
        return this.msg;
    }

    @Override
    public Integer getErrorCode() {
        return this.httpCode;
    }

    public void sendErrorMsg(HttpServletResponse response) throws IOException {
        response.sendError(this.getErrorCode(),this.getErrorMessage());
    }
}
