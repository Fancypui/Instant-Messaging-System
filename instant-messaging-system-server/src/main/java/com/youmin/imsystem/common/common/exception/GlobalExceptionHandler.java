package com.youmin.imsystem.common.common.exception;

import com.youmin.imsystem.common.common.domain.vo.resp.ApiResult;
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

    @ExceptionHandler(value = BusinessException.class)
    public ApiResult<?> businessErrorException(BusinessException e){
        log.info("Business error exception! Reason is {}",e.getMessage());
        return ApiResult.fail(e.getErrorCode(),e.getErrorMsg());
    }

    @ExceptionHandler(value = Exception.class)
    public ApiResult<?> systemError(Throwable e){
          log.error("system error! The reason is {}",e.getMessage());
          return ApiResult.fail(CommonErrorEnum.SYSTEM_ERROR);

    }


}
