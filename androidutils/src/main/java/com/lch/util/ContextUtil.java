package com.lch.util;

import android.app.Application;
import android.content.Context;

public final class ContextUtil {

    private static Context context;
    private static Application application;

    public static void setContext(Context context) {
        ContextUtil.context = context;
    }

    public static Context getContext() {
        return context;
    }

    public static void setApplication(Application application) {
        ContextUtil.application = application;
    }

    public static Application getApplication() {
        return application;
    }
}
