package com.youmin.imsystem.common.user.service.impl;

import com.youmin.imsystem.common.user.cache.UserCache;
import com.youmin.imsystem.common.user.enums.RoleEnum;
import com.youmin.imsystem.common.user.service.IRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;
@Service
public class RoleServiceImpl  implements IRoleService {

    @Autowired
    private UserCache userCache;


    @Override
    public boolean hasPower(Long uid, RoleEnum roleEnum) {
        Set<Long> userRole = userCache.getUserRole(uid);
        return isAdmin(userRole)||userRole.contains(roleEnum.getType());
    }
    private boolean isAdmin(Set<Long> userRoleIds){
        if(userRoleIds.contains(RoleEnum.ADMIN.getType())){
            return true;
        }
        return false;
    }
}
