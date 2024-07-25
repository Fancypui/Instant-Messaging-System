package com.youmin.imsystem.common.common.interceptor;

import com.youmin.imsystem.common.common.domain.dto.RequestInfo;
import com.youmin.imsystem.common.common.exception.HttpErrorEnum;
import com.youmin.imsystem.common.common.utils.RequestHolder;
import com.youmin.imsystem.common.user.cache.UserCache;
import com.youmin.imsystem.common.user.enums.BlackTypeEnum;
import jdk.nashorn.internal.ir.RuntimeNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@Component
public class BlackInterceptor implements HandlerInterceptor {

    @Autowired
    private UserCache userCache;


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Map<Integer, Set<String>> blackRecords = userCache.blackMap();
        RequestInfo requestInfo = RequestHolder.get();
        if(inBlackList(requestInfo.getUid(),blackRecords.get(BlackTypeEnum.UID.getType()))){
            HttpErrorEnum.ACCESS_DENIED.sendErrorMsg(response);
            return false;
        }
        if(inBlackList(requestInfo.getIp(),blackRecords.get(BlackTypeEnum.IP.getType()))){
            HttpErrorEnum.ACCESS_DENIED.sendErrorMsg(response);
            return false;
        }
        return true;
    }
    private boolean inBlackList(Object target,Set<String> blackSet){
        if(Objects.isNull(target)||Objects.isNull(blackSet)){
            return false;
        }
        return blackSet.contains(target.toString());
    }
}
