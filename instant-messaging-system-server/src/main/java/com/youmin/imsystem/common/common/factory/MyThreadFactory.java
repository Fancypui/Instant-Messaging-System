package com.youmin.imsystem.common.common.factory;

import com.youmin.imsystem.common.common.handler.GlobalUncaughtExceptionHandler;
import lombok.AllArgsConstructor;
import org.checkerframework.checker.units.qual.A;

import java.util.concurrent.ThreadFactory;
@AllArgsConstructor
public class MyThreadFactory implements ThreadFactory {




    private final ThreadFactory original;

    @Override
    public Thread newThread(Runnable r) {
        Thread thread = original.newThread(r);
        thread.setUncaughtExceptionHandler(GlobalUncaughtExceptionHandler.getInstance());
        return thread;
    }
}
