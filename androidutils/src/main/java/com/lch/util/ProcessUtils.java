package com.lch.util;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.util.List;

/**
 * util for process
 */
public class ProcessUtils {

    private static Boolean isUiProcess;//cache.

    public static boolean isUIprocess(Context context) {
        if (isUiProcess == null) {
            isUiProcess = isUIprocessImpl(context);
        }

        return isUiProcess;
    }

    private static boolean isUIprocessImpl(Context context) {
        try {
            String processName = currentProcessName();
            if (!TextUtils.isEmpty(processName)) {
                return processName.equals(context.getPackageName());
            }

            ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager
                    .getRunningAppProcesses();
            if (appProcesses == null)
                return false;
            for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
                if (appProcess.pid == android.os.Process.myPid()) {
                    return appProcess.processName.equals(context.getPackageName());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;

    }

    public static String currentProcessName() {
        try {
            Class cls = Class.forName("android.app.ActivityThread");
            Method currentProcessName_method = cls.getDeclaredMethod("currentProcessName");
            currentProcessName_method.setAccessible(true);
            Object currentProcessName = currentProcessName_method.invoke(null);
            return currentProcessName.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean isMainProcess(Context context) {
        String processName = getProcessName(getAppProcessId());
        return processName != null && processName.equals(context.getPackageName());
    }

    public static String getProcessName(int pid) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader("/proc/" + pid + "/cmdline"));
            String processName = reader.readLine();
            if (!TextUtils.isEmpty(processName)) {
                processName = processName.trim();
            }
            return processName;
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 获取当前进程名
     * @param context
     * @return 进程名
     */
    public static String getProcessName(Context context) {
        String processName = null;
        // ActivityManager
        ActivityManager am = ((ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE));
        while (true) {
            for (ActivityManager.RunningAppProcessInfo info : am.getRunningAppProcesses()) {
                if (info.pid == getAppProcessId()) {
                    processName = info.processName;
                    break;
                }
            }
            // go home
            if (!TextUtils.isEmpty(processName)) {
                return processName;
            }
            // take a rest and again
            try {
                Thread.sleep(100L);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * 判断class对应的Service是否开启
     * @param context
     * @param className service 全称
     *
     *                   获取当前手机运行的前200个服务
     * @return
     */
    public static boolean isServiceRunning(Context context, String className) {
        boolean isRunning = false;
        if (!TextUtils.isEmpty(className)) {
            try {
                ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
                List<ActivityManager.RunningServiceInfo> serviceList = activityManager.getRunningServices(200);
                if (serviceList == null || !(serviceList.size() > 0)) {
                    return false;
                }
                for (int i = 0; i < serviceList.size(); i++) {
                    ComponentName service = serviceList.get(i).service;
                    if (service != null && className.equals(service.getClassName())) {
                        isRunning = true;
                        break;
                    }
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
        return isRunning;
    }

    /**
     * 获取当前App进程的id
     *
     * @return pid
     */
    public static int getAppProcessId() {
        return android.os.Process.myPid();
    }

    /**
     * 获取当前正在进行的进程数
     *
     * @param context
     * @return
     */
    public static int getRunningAppProcessInfoSize(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        return am.getRunningAppProcesses().size();
    }

    /**
     * 获取系统可用内存
     *
     * @param context
     * @return
     */
    public static long getAvailMem(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        //得到可用内存
        ActivityManager.MemoryInfo outInfo = new ActivityManager.MemoryInfo();
        am.getMemoryInfo(outInfo);
        long availMem = outInfo.availMem; //单位是byte
        return availMem;
    }

    /**
     * 获取系统所有的进程信息列表
     *
     * @param context
     * @return
     */
    public static List<ActivityManager.RunningAppProcessInfo> getRunningAppProcessInfos(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        return am.getRunningAppProcesses();
    }

    public static int getParentPid(int pid) {
        String[] procStatusLabels = {"PPid:"};
        long[] procStatusValues = new long[1];
        procStatusValues[0] = -1;
        readProcLines("/proc/" + pid + "/status", procStatusLabels, procStatusValues);
        return (int) procStatusValues[0];
    }

    public static int getUidForPid(int pid) {
        String[] procStatusLabels = {"Uid:"};
        long[] procStatusValues = new long[1];
        procStatusValues[0] = -1;
        readProcLines("/proc/" + pid + "/status", procStatusLabels, procStatusValues);
        return (int) procStatusValues[0];
    }

    public static String getPidCmdline(int pid) {
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(String.format("/proc/%1$s/cmdline", pid)));
            return br.readLine().trim();
        } catch (FileNotFoundException e) {
        } catch (IOException e) {
        } finally {
            try {
                br.close();
            } catch (Exception e) {
            }
        }
        return getCommandLineOutput(String.format("cat /proc/%1$s/cmdline", pid)).trim();
    }

    public static String getCommandLineOutput(String cmdLine) {
        String output = "";
        try {
            Process p = Runtime.getRuntime().exec(cmdLine);
            BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line;
            while ((line = input.readLine()) != null) {
                output += (line + '\n');
            }
            input.close();
        } catch (Exception e) {
        }
        return output;
    }

    public static void readProcLines(String path, String[] reqFields, long[] outSize) {
        try {
            Class<Process> cls = Process.class;
            Method mReadProcLines = cls.getDeclaredMethod("readProcLines", String.class, String[].class, long[].class);
            mReadProcLines.invoke(null, path, reqFields, outSize);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
