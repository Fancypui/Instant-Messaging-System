package com.youmin.imsystem.common.common.domain.vo.resp;

import com.youmin.imsystem.common.common.exception.ErrorEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("Base Response Body")
public class ApiResult<T> {
    @ApiModelProperty("To tell whether a particular request is success or fail")
    private Boolean success;
    @ApiModelProperty("Error Code")
    private Integer errCode;
    @ApiModelProperty("Error Message")
    private String errMsg;
    @ApiModelProperty("Actual Data")
    private T data;

    public static <T> ApiResult<T> success() {
        ApiResult<T> result = new ApiResult<T>();
        result.setData(null);
        result.setSuccess(Boolean.TRUE);
        return result;
    }

    public static <T> ApiResult<T> success(T data) {
        ApiResult<T> result = new ApiResult<T>();
        result.setData(data);
        result.setSuccess(Boolean.TRUE);
        return result;
    }

    public static <T> ApiResult<T> fail(Integer code, String msg) {
        ApiResult<T> result = new ApiResult<T>();
        result.setSuccess(Boolean.FALSE);
        result.setErrCode(code);
        result.setErrMsg(msg);
        return result;
    }

    public static <T> ApiResult<T> fail(ErrorEnum errorEnum) {
        ApiResult<T> result = new ApiResult<T>();
        result.setSuccess(Boolean.FALSE);
        result.setErrCode(errorEnum.getErrorCode());
        result.setErrMsg(errorEnum.getErrorMessage());
        return result;
    }

    public boolean isSuccess() {
        return this.success;
    }
}