package com.youmin.imsystem.common.common.utils;

import com.youmin.imsystem.common.common.domain.dto.RequestInfo;

/**
 * A utils consist of a threadLocal,
 * where the threadLocal is used to store uid and ip collected from HTTP_REQUEST
 */
public class RequestHolder {
    private static final ThreadLocal<RequestInfo> threadLocal = new ThreadLocal<>();

    public static void set(RequestInfo requestInfo){
        threadLocal.set(requestInfo);
    }

    public static RequestInfo get(){
        return threadLocal.get();
    }

    public static void remove(){
        threadLocal.remove();
    }

}
