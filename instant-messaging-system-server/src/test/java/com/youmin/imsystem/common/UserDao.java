package com.youmin.imsystem.common;


import com.youmin.imsystem.common.user.domain.entity.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
public class UserDao {

    @Autowired
    private com.youmin.imsystem.common.user.dao.UserDao userDao;

    @Test
    public void test(){
        User byId = userDao.getById(1);
        System.out.println(byId);
    }
}
