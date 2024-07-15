package com.youmin.imsystem.common;


import com.youmin.imsystem.common.user.domain.entity.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.TimeUnit;

@SpringBootTest
@RunWith(SpringRunner.class)
public class UserDao {

    @Autowired
    private com.youmin.imsystem.common.user.dao.UserDao userDao;



    @Autowired
    private RedisTemplate redisTemplate;
    @Test
    public void redis() {
        redisTemplate.opsForValue().getAndDelete("name");
        String name = (String) redisTemplate.opsForValue().get("name");
        System.out.println(name);
    }

    @Test
    public void test(){
        User byId = userDao.getById(1);
        System.out.println(byId);
    }
}
