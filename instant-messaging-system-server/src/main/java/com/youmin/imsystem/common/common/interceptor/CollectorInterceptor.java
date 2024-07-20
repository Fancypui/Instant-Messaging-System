package com.youmin.imsystem.common.common.interceptor;

import cn.hutool.extra.servlet.ServletUtil;
import com.youmin.imsystem.common.common.domain.dto.RequestInfo;
import com.youmin.imsystem.common.common.utils.RequestHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

/**
 * Message Collect Interceptor
 */
@Component
public class CollectorInterceptor implements HandlerInterceptor {


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        RequestInfo requestInfo = new RequestInfo();
        requestInfo.setUid(Optional.ofNullable(request.getAttribute(TokenInterceptor.UID)).map(Object::toString).map(Long::parseLong).orElse(null));
        requestInfo.setIp(ServletUtil.getClientIP(request));
        RequestHolder.set(requestInfo);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        RequestHolder.remove();
    }
}
