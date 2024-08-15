package com.youmin.imsystem.transaction.aspect;

import cn.hutool.core.date.DateUtil;
import com.youmin.imsystem.transaction.annotation.SecureInvoke;
import com.youmin.imsystem.common.utils.JsonUtils;
import com.youmin.imsystem.transaction.domain.dto.SecureInvokeDTO;
import com.youmin.imsystem.transaction.domain.entity.SecureInvokeRecord;
import com.youmin.imsystem.transaction.service.SecureInvokeHolder;
import com.youmin.imsystem.transaction.service.SecureInvokeService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Aspect
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 1)
public class SecureInvokeAspect {

    @Autowired
    private SecureInvokeService secureInvokeService;


    @Around("@annotation(com.youmin.imsystem.transaction.annotation.SecureInvoke)")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        boolean inTransaction = TransactionSynchronizationManager.isActualTransactionActive();
        //if current execution not in transaction, execute it immediately
        if(SecureInvokeHolder.isInvoking()|| !inTransaction){
            return joinPoint.proceed();
        }
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        SecureInvoke secureInvoke = method.getAnnotation(SecureInvoke.class);
        boolean async = secureInvoke.async();
        boolean callbackRemove = secureInvoke.callBackRemove();
        Class<?> executionClass = method.getDeclaringClass();
        List<String> parameterClasses = Arrays.stream(method.getParameterTypes()).map(Class::getName).collect(Collectors.toList());
        SecureInvokeDTO secureInvokeDTO = SecureInvokeDTO
                .builder()
                .className(executionClass.getName())
                .methodName(method.getName())
                .parameterTypes(JsonUtils.toStr(parameterClasses))
                .args(JsonUtils.toStr(joinPoint.getArgs()))
                .build();
        SecureInvokeRecord record = SecureInvokeRecord
                .builder()
                .secureInvokeDTO(secureInvokeDTO)
                .nextRetryTime(DateUtil.offsetMinute(new Date(), (int) SecureInvokeService.RETRY_INTERVAL_MINUTES))
                .maxRetryTimes(secureInvoke.maxRetryTimes())
                .callBackRemove(secureInvoke.callBackRemove())
                .build();
        secureInvokeService.invoke(record,async);
        return null;

    }


}
