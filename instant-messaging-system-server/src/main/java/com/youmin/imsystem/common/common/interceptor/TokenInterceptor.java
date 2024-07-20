package com.youmin.imsystem.common.common.interceptor;

import com.youmin.imsystem.common.common.exception.HttpErrorEnum;
import com.youmin.imsystem.common.user.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;
import java.util.Optional;

@Component
public class TokenInterceptor implements HandlerInterceptor {

    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String BEARER = "Bearer ";
    public static final String UID = "UID";

    @Autowired
    private LoginService loginService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //get user token
        String token = getToken(request);
        Long validUid = loginService.getValidUid(token);
        if(Objects.nonNull(validUid)){
            request.setAttribute(UID,validUid);
        }else{
            boolean isPublic = isPublicUrl(request.getRequestURI());
            if(!isPublic){//send 404 if the uri is not public and user does not have valid token
                HttpErrorEnum.ACCESS_DENIED.sendErrorMsg(response);
                return false;
            }
        }

        return true;
    }


    private boolean isPublicUrl(String uri){
        String[] split = uri.split("/");
        return split.length>2 && "public".equals(split[3]);
    }

    private String extractTokenValue(String token){
        return token.replace(BEARER,"");
    }

    private String getToken(HttpServletRequest request){
        String header = request.getHeader(AUTHORIZATION_HEADER);
        String token = Optional.of(header)
                .filter(k -> k.startsWith(BEARER))
                .map(k -> k.substring(BEARER.length()))
                .orElse(null);
        return token;
    }
}
