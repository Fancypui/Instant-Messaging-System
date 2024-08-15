package com.youmin.imsystem.common.common.aspect;

import com.youmin.imsystem.common.common.annotation.RedissonLock;
import com.youmin.imsystem.common.common.service.LockService;
import com.youmin.imsystem.common.utils.SpElUtils;
import jodd.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Slf4j
@Aspect
@Component
@Order(0)
public class RedissonLockAspect {
    @Autowired
    private LockService lockService;

    /**
     * RedissonLockAdvice
     */
    @Around("@annotation(com.youmin.imsystem.common.common.annotation.RedissonLock)")
    public void redissonLock(ProceedingJoinPoint joinPoint) throws Throwable {
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        RedissonLock redissonLockAnnotation = method.getAnnotation(RedissonLock.class);//get redissonLock annotation instance so as to get value
        //get key prefix
        String prefix = StringUtil.isBlank(redissonLockAnnotation.prefix())?SpElUtils.getMethodKey(method):redissonLockAnnotation.prefix();
        //get actual key
        String key = SpElUtils.getKey(redissonLockAnnotation.key(), method, joinPoint.getArgs());
        //invoke lockService to get distributed lock, thereby executing business logic
        lockService.executeWithLock(prefix+":"+key, redissonLockAnnotation.waitTime(), redissonLockAnnotation.unit(),joinPoint::proceed);

    }


}
