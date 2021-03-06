package com.aliya.compat;

import java.util.concurrent.TimeoutException;

/**
 * 解决 Object#finalize() - java.util.concurrent.TimeoutException 异常
 *
 * @author a_liYa
 * @date 2019/5/19 10:12.
 */
public final class CrashCompat {

    public static void fixBug() {
        new UncaughtHandler();
    }

    static final class UncaughtHandler implements Thread.UncaughtExceptionHandler {

        Thread.UncaughtExceptionHandler mDefaultUncaughtHandler;

        public UncaughtHandler() {
            if (!(Thread.getDefaultUncaughtExceptionHandler() instanceof UncaughtHandler)) {
                mDefaultUncaughtHandler = Thread.getDefaultUncaughtExceptionHandler();
                Thread.setDefaultUncaughtExceptionHandler(this);
            }
        }

        @Override
        public void uncaughtException(Thread t, Throwable e) {
            if ("FinalizerWatchdogDaemon".equals(t.getName()) && e instanceof TimeoutException) {
                // ignore it. java.util.concurrent.TimeoutException - xxx.finalize()
            } else if (mDefaultUncaughtHandler != Thread.getDefaultUncaughtExceptionHandler()){
                if (mDefaultUncaughtHandler != null) {
                    mDefaultUncaughtHandler.uncaughtException(t, e);
                }
            }
        }
    }
}
