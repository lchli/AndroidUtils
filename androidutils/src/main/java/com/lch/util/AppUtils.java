package com.lch.util;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;

public final class AppUtils {


    /**
     * Return the application's version name.
     *
     * @return the application's version name
     */
    public static String getAppVersionName() {
        return getAppVersionName(ContextUtil.getContext().getPackageName());
    }

    /**
     * Return the application's version name.
     *
     * @param packageName The name of the package.
     * @return the application's version name
     */
    public static String getAppVersionName(final String packageName) {
        try {
            PackageManager pm = ContextUtil.getContext().getPackageManager();
            PackageInfo pi = pm.getPackageInfo(packageName, 0);
            return pi == null ? null : pi.versionName;
        } catch (Throwable e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * Return the application's version code.
     *
     * @return the application's version code
     */
    public static int getAppVersionCode() {
        return getAppVersionCode(ContextUtil.getContext().getPackageName());
    }

    /**
     * Return the application's version code.
     *
     * @param packageName The name of the package.
     * @return the application's version code
     */
    public static int getAppVersionCode(final String packageName) {
        try {
            PackageManager pm = ContextUtil.getContext().getPackageManager();
            PackageInfo pi = pm.getPackageInfo(packageName, 0);
            return pi == null ? -1 : pi.versionCode;
        } catch (Throwable e) {
            e.printStackTrace();
            return -1;
        }
    }


    public static boolean isTabletDevice() {
        try {
            return (ContextUtil.getContext().getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >=
                    Configuration.SCREENLAYOUT_SIZE_LARGE;
        } catch (Throwable e) {
            e.printStackTrace();
            return false;
        }
    }

    public static String getAppName() {
        try {
            return ContextUtil.getContext().getApplicationInfo().loadLabel(ContextUtil.getContext().getPackageManager()).toString();
        } catch (Throwable e) {
            e.printStackTrace();
            return "";
        }
    }

}
