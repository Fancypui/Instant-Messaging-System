package com.youmin.imsystem.transaction.service;

import java.util.Objects;

public class SecureInvokeHolder {
    private static final ThreadLocal<Boolean> INVOKE_THREAD_LOCAL = new ThreadLocal<>();

    public static boolean isInvoking() {
        return Objects.nonNull(INVOKE_THREAD_LOCAL.get());
    }

    public static void setInvoking(){
        INVOKE_THREAD_LOCAL.set(Boolean.TRUE);
    }
    public static final void invoked(){
        INVOKE_THREAD_LOCAL.remove();
    }
}
