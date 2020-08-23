package com.lch.util.executor;

import android.os.Handler;
import android.os.Looper;

public class UiHander {
    private static Handler handler = new Handler(Looper.getMainLooper());

    public static void run(Runnable r) {
        if (Looper.getMainLooper() == Looper.myLooper()) {
            r.run();
        } else {
            handler.post(r);
        }
    }

    public static void run(Runnable r, long delayMills) {
        handler.postDelayed(r, delayMills);
    }

    public static Handler getHandler() {
        return handler;
    }
}
