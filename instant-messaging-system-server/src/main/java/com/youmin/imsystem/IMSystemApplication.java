package com.youmin.imsystem;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication(scanBasePackages = {"com.youmin.imsystem"})
@MapperScan({"com.youmin.imsystem.common.**.mapper"})

public class IMSystemApplication {
    public static void main(String[] args) {
        SpringApplication.run(IMSystemApplication.class,args);
    }
}
