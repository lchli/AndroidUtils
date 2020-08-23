package com.lch.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.support.annotation.RequiresPermission;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.util.Log;


import com.lch.util.executor.ResultDto;
import com.lch.util.http.HttpUtils;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.Manifest.permission.ACCESS_WIFI_STATE;
import static android.Manifest.permission.INTERNET;

public final class IPUtils {

    /**
     * Return the ip address.
     * <p>Must hold {@code <uses-permission android:name="android.permission.INTERNET" />}</p>
     *
     * @param useIPv4 True to use ipv4, false otherwise.
     * @return the ip address
     */
    @RequiresPermission(INTERNET)
    public static String getIPAddress(final boolean useIPv4) {
        try {
            Enumeration<NetworkInterface> nis = NetworkInterface.getNetworkInterfaces();
            LinkedList<InetAddress> adds = new LinkedList<>();
            while (nis.hasMoreElements()) {
                NetworkInterface ni = nis.nextElement();
                // To prevent phone of xiaomi return "10.0.2.15"
                if (!ni.isUp() || ni.isLoopback()) continue;
                Enumeration<InetAddress> addresses = ni.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    adds.addFirst(addresses.nextElement());
                }
            }
            for (InetAddress add : adds) {
                if (!add.isLoopbackAddress()) {
                    String hostAddress = add.getHostAddress();
                    boolean isIPv4 = hostAddress.indexOf(':') < 0;
                    if (useIPv4) {
                        if (isIPv4) return hostAddress;
                    } else {
                        if (!isIPv4) {
                            int index = hostAddress.indexOf('%');
                            return index < 0
                                    ? hostAddress.toUpperCase()
                                    : hostAddress.substring(0, index).toUpperCase();
                        }
                    }
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return "";
    }


    /**
     * Return the ip address by wifi.
     *
     * @return the ip address by wifi
     */
    @RequiresPermission(ACCESS_WIFI_STATE)
    public static String getIpAddressByWifi() {
        @SuppressLint("WifiManagerLeak")
        WifiManager wm = (WifiManager) ContextUtil.getContext().getSystemService(Context.WIFI_SERVICE);
        if (wm == null) return "";
        return Formatter.formatIpAddress(wm.getDhcpInfo().ipAddress);
    }

    @SuppressLint("MissingPermission")
    public static String getIP() {
        try {
            String ip = getIPAddress(true);
            if (TextUtils.isEmpty(ip)) {
                ip = getIpAddressByWifi();
            }
            return ip;
        } catch (Throwable e) {
            e.printStackTrace();
        }

        return "";
    }


    public static String getNetIP() {
        try {
            ResultDto<String> res = HttpUtils.get("http://pv.sohu.com/cityjson?ie=utf-8", null, null);
            String body = res.getData();
            Pattern pattern = Pattern
                    .compile("((?:(?:25[0-5]|2[0-4]\\d|((1\\d{2})|([1-9]?\\d)))\\.){3}(?:25[0-5]|2[0-4]\\d|((1\\d{2})|([1-9]?\\d))))");
            Matcher matcher = pattern.matcher(body);
            if (matcher.find()) {
                String ip = matcher.group();
                Log.d("ip", "net IP:" + ip);
                return ip;
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }

        return "";
    }


}
