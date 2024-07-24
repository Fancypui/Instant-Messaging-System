package com.youmin.imsystem.common.user.domain.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Objects;

@Data
public class IpResult<T> implements Serializable {
    private static final long serialVersionUID = 1L;
    @ApiModelProperty("error code")
    private Integer code;
    @ApiModelProperty("error msg")
    private String msg;
    @ApiModelProperty("result")
    private T data;

    public boolean isSuccess() {
        return Objects.nonNull(this.code) && this.code == 0;
    }
}
