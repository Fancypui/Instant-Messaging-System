package com.youmin.imsystem.common.user.service.impl;

import com.youmin.imsystem.common.user.dao.UserDao;
import com.youmin.imsystem.common.user.domain.entity.User;
import com.youmin.imsystem.common.user.service.UserService;
import com.youmin.imsystem.common.user.service.adapter.UserAdapter;
import me.chanjar.weixin.common.bean.WxOAuth2UserInfo;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDao userDao;


    @Override
    @Transactional
    public Long registered(User user) {
        userDao.save(user);
        return user.getId();
        //todo emit to subscribers who subscribe this user registered event
    }


}
