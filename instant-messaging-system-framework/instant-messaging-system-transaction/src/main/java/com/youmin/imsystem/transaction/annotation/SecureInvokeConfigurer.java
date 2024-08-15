package com.youmin.imsystem.transaction.annotation;

import io.micrometer.core.lang.Nullable;

import java.util.concurrent.Executor;

public interface SecureInvokeConfigurer {

    @Nullable
    default Executor getSecureInvokeExecutor(){
        return null;
    }
}
