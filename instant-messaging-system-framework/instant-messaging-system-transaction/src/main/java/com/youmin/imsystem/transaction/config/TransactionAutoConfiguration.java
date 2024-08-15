package com.youmin.imsystem.transaction.config;

import com.youmin.imsystem.transaction.annotation.SecureInvokeConfigurer;
import com.youmin.imsystem.transaction.aspect.SecureInvokeAspect;
import com.youmin.imsystem.transaction.dao.SecureInvokeRecordDao;
import com.youmin.imsystem.transaction.mapper.SecureInvokeRecordMapper;
import com.youmin.imsystem.transaction.service.MQProducer;
import com.youmin.imsystem.transaction.service.SecureInvokeService;
import io.micrometer.core.lang.Nullable;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.util.CollectionUtils;
import org.springframework.util.function.SingletonSupplier;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Collectors;

@Configuration
//@EnableScheduling
@MapperScan(basePackageClasses = SecureInvokeRecordMapper.class)
@Import({SecureInvokeAspect.class,SecureInvokeRecordDao.class})
public class TransactionAutoConfiguration {

    @Nullable
    private Executor executor;


    @Autowired
    void setConfigurers(ObjectProvider<SecureInvokeConfigurer> configurers){
        SingletonSupplier<SecureInvokeConfigurer> configurer = SingletonSupplier.of(() -> {
            List<SecureInvokeConfigurer> candidates = configurers.stream().collect(Collectors.toList());
            if (CollectionUtils.isEmpty(candidates)) {
                return null;
            }
            if (candidates.size() > 1) {
                throw new IllegalStateException("Only one SecureInvokeConfigurer may exist");
            }
            return candidates.get(0);
        });
        executor = Optional.ofNullable(configurer.get()).map(SecureInvokeConfigurer::getSecureInvokeExecutor)
                .orElse(ForkJoinPool.commonPool());
    }

    @Bean
    public SecureInvokeService getSecureInvokeService(SecureInvokeRecordDao dao){
        return new SecureInvokeService(dao,executor);
    }

    @Bean
    public MQProducer getMQProducer(){
        return new MQProducer();
    }
}
