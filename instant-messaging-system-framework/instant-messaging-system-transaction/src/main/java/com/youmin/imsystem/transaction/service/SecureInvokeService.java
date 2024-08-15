package com.youmin.imsystem.transaction.service;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.youmin.imsystem.common.utils.JsonUtils;
import com.youmin.imsystem.transaction.dao.SecureInvokeRecordDao;
import com.youmin.imsystem.transaction.domain.dto.SecureInvokeDTO;
import com.youmin.imsystem.transaction.domain.entity.SecureInvokeRecord;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.lang.reflect.Method;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

/**
 * secure invoke handler
 */
@Slf4j
@AllArgsConstructor
public class SecureInvokeService {

    public static final double RETRY_INTERVAL_MINUTES = 2D;

    private final SecureInvokeRecordDao secureInvokeRecordDao;

    private final Executor executor;

//    @Scheduled(cron = "*/5 * * * * ?")
    public void retry(){
        List<SecureInvokeRecord> secureInvokeRecords = secureInvokeRecordDao.getWaitRetryRecords();
        for (SecureInvokeRecord secureInvokeRecord : secureInvokeRecords) {
            doAsyncInvoke(secureInvokeRecord);
        }
    }

    public void invoke(SecureInvokeRecord record, boolean async) {
        boolean inTransaction = TransactionSynchronizationManager.isActualTransactionActive();
        if (!inTransaction) {
            return;
        }
        //save execution details
        secureInvokeRecordDao.save(record);
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                if(async){
                    doAsyncInvoke(record);
                }else {
                    doInvoke(record);
                }
            }

        });
    }
    private void doInvoke(SecureInvokeRecord record) {
        SecureInvokeDTO secureInvokeDTO = record.getSecureInvokeDTO();
        try{
            SecureInvokeHolder.setInvoking();
            Class<?> executionClass = Class.forName(secureInvokeDTO.getClassName());
            Object bean = SpringUtil.getBean(executionClass);
            List<String> parameterString = JsonUtils.toList(secureInvokeDTO.getParameterTypes(),String.class);
            List<Class<?>> parameterClasses = getParameterClasses(parameterString);
            Method method = ReflectUtil.getMethod(executionClass, secureInvokeDTO.getMethodName(), parameterClasses.toArray(new Class[]{}));
            Object[] args = getArgs(secureInvokeDTO, parameterClasses);
            //execute method
            method.invoke(bean,args);

            //execute successful, remove record
            removeRecord(record.getId());

        }catch (Throwable throwable){
            log.error("SecureInvokeService invoke fail", throwable);
            //execute failure, update next retry time
            retryRecord(record, throwable.getMessage());
        }finally {
            SecureInvokeHolder.invoked();
        }
    }

    private void retryRecord(SecureInvokeRecord record, String errorMsg) {
        Integer retryTimes = record.getRetryTimes() + 1;
        SecureInvokeRecord update = new SecureInvokeRecord();
        update.setId(record.getId());
        update.setFailReason(errorMsg);
        update.setNextRetryTime(getNextRetryTime(retryTimes));
        if (retryTimes > record.getMaxRetryTimes()) {
            update.setStatus(SecureInvokeRecord.STATUS_FAIL);
        } else {
            update.setRetryTimes(retryTimes);
        }
        secureInvokeRecordDao.updateById(update);
    }

    private Date getNextRetryTime(Integer retryTimes) {
        double waitMinutes = Math.pow(RETRY_INTERVAL_MINUTES, retryTimes);
        return DateUtil.offsetMinute(new Date(), (int) waitMinutes);
    }

    private void removeRecord(Long id) {
        secureInvokeRecordDao.removeById(id);
    }

    private Object[] getArgs(SecureInvokeDTO secureInvokeDTO, List<Class<?>> parameterClasses){
        JsonNode jsonNode = JsonUtils.toJsonNode(secureInvokeDTO.getArgs());
        Object[] args = new Object[jsonNode.size()];
        for (int i = 0; i < args.length; i++) {
            Class<?> aClass = parameterClasses.get(i);
            args[i] = JsonUtils.nodeToValue(jsonNode.get(i), aClass);
        }
        return args;
    }

    private void doAsyncInvoke(SecureInvokeRecord record) {
        executor.execute(()->{
            System.out.println(Thread.currentThread().getName());
            doInvoke(record);
        });
    }

    private List<Class<?>> getParameterClasses(List<String> parameterString){
        return parameterString
                .stream()
                .map(name -> {
                    try{
                       return Class.forName(name);
                    }catch (ClassNotFoundException e){
                        log.error("SecureInvokeService class not fund", e);
                    }
                    return null;
                }).collect(Collectors.toList());
    }
}
