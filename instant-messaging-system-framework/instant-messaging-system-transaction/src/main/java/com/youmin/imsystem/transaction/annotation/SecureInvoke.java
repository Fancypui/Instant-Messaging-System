package com.youmin.imsystem.transaction.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface SecureInvoke {

    /**
     * whether to execute the operation securely and asynchronously
     * @return
     */
    boolean async() default true;

    /**
     * no of retry if execution failure (including the first execution)
     * @return
     */
    int maxRetryTimes() default 3;

    /**
     * rabbitmq provide features like publisher confirm to ensure the message reach queue or exchange
     * so cannot immediately remove the record if message is successfully sent using convertAndSend()
     * @return
     */
     boolean callBackRemove() default false;
}
