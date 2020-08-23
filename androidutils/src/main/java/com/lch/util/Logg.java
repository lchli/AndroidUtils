package com.lch.util;

import android.util.Log;

import java.io.PrintWriter;
import java.io.StringWriter;

public final class Logg {
    private String logPrefix = "";
    private boolean isOpenLog = false;

    public Logg() {
    }

    public void setLogPrefix(String logPrefix) {
        this.logPrefix = logPrefix;
    }

    public void setOpenLog(boolean openLog) {
        this.isOpenLog = openLog;
    }

    public void log(Object msg) {
        this.log("", (Object) msg);
    }

    public void log(Object tag, Object msg) {
        if (this.isOpenLog && msg != null && tag != null) {
            Log.e(tag.toString() + this.logPrefix, msg.toString());
        }

    }

    public void log(Object tag, Throwable e) {
        String msg = getExceptionMsg(e);
        if (msg != null && tag != null) {
            Log.e(tag.toString() + this.logPrefix, msg);
        }

    }

    public void logJava(Object tag, Throwable e) {
        System.err.println(tag + "::" + getExceptionMsg(e));
    }

    public void logJava(Object tag, String msg) {
        System.err.println(tag + "::" + msg);
    }

    public boolean isOpenLog() {
        return isOpenLog;
    }

    private static String getExceptionMsg(Throwable e) {
        if (e == null) {
            return "null";
        }
        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw));
        sw.flush();
        String msg = sw.toString();
        IOUtils.closeQuietly(sw);
        return msg;
    }
}