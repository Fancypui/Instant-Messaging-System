package com.youmin.imsystem.common.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface RedissonLock {

    /**
     * key prefix; if prefix is not passed in, it will be classPath+methodName
     * @return
     */
    String prefix() default "";

    /**
     * Actual business key for redisson
     * @return
     */
    String key() default "";

    /**
     * Wait time
     * @return
     */
    int waitTime() default -1;

    TimeUnit unit() default TimeUnit.MILLISECONDS;
}
