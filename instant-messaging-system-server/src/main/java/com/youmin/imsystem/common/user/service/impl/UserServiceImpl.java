package com.youmin.imsystem.common.user.service.impl;

import com.youmin.imsystem.common.user.dao.ItemConfigDao;
import com.youmin.imsystem.common.user.dao.UserBackpackDao;
import com.youmin.imsystem.common.user.dao.UserDao;
import com.youmin.imsystem.common.user.domain.entity.User;
import com.youmin.imsystem.common.user.domain.entity.UserBackpack;
import com.youmin.imsystem.common.user.domain.vo.req.ModifyNameReq;
import com.youmin.imsystem.common.user.domain.vo.resp.UserInfoResp;
import com.youmin.imsystem.common.user.enums.ItemEnum;
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

    @Autowired
    private UserBackpackDao userBackpackDao;



    @Override
    @Transactional
    public Long registered(User user) {
        userDao.save(user);
        return user.getId();
        //todo emit to subscribers who subscribe this user registered event
    }

    @Override
    public UserInfoResp getUserInfo(Long uid) {
        User user = userDao.getById(uid);
        Integer count =
                userBackpackDao.getModifyNameCardCount(ItemEnum.MODIFY_NAME_CARD.getItemId(), uid);
        return UserAdapter.buildUserInfoResp(user,count);
    }

    @Override
    public void modifyName(Long uid, ModifyNameReq modifyNameReq) {

    }


}
