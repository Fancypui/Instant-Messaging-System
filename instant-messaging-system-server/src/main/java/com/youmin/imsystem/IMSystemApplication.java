package com.youmin.imsystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.youmin.imsystem"})
public class IMSystemApplication {
    public static void main(String[] args) {
        SpringApplication.run(IMSystemApplication.class,args);
    }
}
