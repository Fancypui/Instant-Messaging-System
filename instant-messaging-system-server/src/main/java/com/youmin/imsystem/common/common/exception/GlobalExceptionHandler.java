package com.youmin.imsystem.common.common.exception;

import com.youmin.imsystem.common.common.domain.vo.resp.ApiResult;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ApiResult<?> methodArgumentNotValidException(MethodArgumentNotValidException e){
        StringBuilder errorMsg = new StringBuilder();
        e.getBindingResult().getFieldErrors().forEach(x->errorMsg.append(x.getField()).append(x.getDefaultMessage()));
        String message = errorMsg.toString();
        return ApiResult.fail(CommonErrorEnum.PARAM_VALID.getCode(),message);
    }

    @ExceptionHandler(value = Exception.class)
    public ApiResult<?> systemError(Throwable e){
          log.error("system error! The reason is {}",e.getMessage());
          return ApiResult.fail(CommonErrorEnum.SYSTEM_ERROR);

    }
}
