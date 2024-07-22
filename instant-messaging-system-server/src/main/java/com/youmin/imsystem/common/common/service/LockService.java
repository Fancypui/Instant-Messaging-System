package com.youmin.imsystem.common.common.service;

import com.youmin.imsystem.common.common.exception.BusinessException;
import com.youmin.imsystem.common.common.exception.CommonErrorEnum;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.concurrent.TimeUnit;

/**
 * Distributed Lock Service
 */
@Component
public class LockService {
    @Autowired
    private RedissonClient redissonClient;

    /**
     * Redisson lock with expire
     * @param key
     * @param waitTime
     * @param unit
     * @param supplier
     * @return
     * @param <T>
     * @throws Throwable
     */
    public <T> T executeWithLock(String key, Integer waitTime, TimeUnit unit, Supplier<T> supplier) throws Throwable {
        RLock lock = redissonClient.getLock(key);
        boolean isLocked = lock.tryLock(waitTime, unit);//acquire lock
        if(!isLocked){
            throw new BusinessException(CommonErrorEnum.LOCK_LIMIT);
        }
        try{
            return supplier.get();//execute business logic
        }finally {
            lock.unlock();//release lock
        }
    }

    /**
     * This lock is same with the first overloading method, but does not have try lock expire time
     * @param key
     * @param supplier
     * @return
     * @param <T>
     * @throws Throwable
     */
    public <T> T executeWithLock(String key, Supplier<T> supplier) throws Throwable {
        return executeWithLock(key, -1, TimeUnit.MILLISECONDS,supplier);
    }
    @FunctionalInterface
    public interface Supplier<T>{

        /**
         * Gets a result.
         *
         * @return a result
         */
        T get()  throws Throwable;
    }

}
