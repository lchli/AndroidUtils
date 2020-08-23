package com.lch.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.webkit.WebSettings;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import static android.content.Context.WIFI_SERVICE;

@SuppressLint("MissingPermission")
public final class ApiUtil {
    private static String imei;
    private static String mac;
    private static String userAgent;

    public static boolean isAdApiCodeSuccess(String code) {
        return "0".equals(code);
    }


    public static Map<String, String> getAdApiCommonParams() {
        Map<String, String> params = new HashMap<>();

        try {
            params.put("osv", Build.VERSION.RELEASE);
            params.put("bundle", ContextUtil.getContext().getPackageName());
            params.put("appv", AppUtils.getAppVersionName());
            params.put("devt", AppUtils.isTabletDevice() ? "2" : "1");
            params.put("vendor", urlEncode(Build.BRAND));
            params.put("model", urlEncode(Build.MODEL));

            if (TextUtils.isEmpty(imei)) {
                imei = PhoneUtils.getIMEI();
            }

            if (TextUtils.isEmpty(mac)) {
                mac = DeviceUtils.getMacAddress();
            }

            params.put("imei", imei);
            params.put("mac", mac);
            params.put("androidid", DeviceUtils.getAndroidID());
            params.put("sw", ScreenUtils.getScreenWidth() + "");
            params.put("sh", ScreenUtils.getScreenHeight() + "");
            params.put("ost", "1");
            params.put("opert", getSimOperatorInfo() + "");
            params.put("connt", getNetworkType());
            params.put("sdk", "true");

            Location loc = SystemLocationUtil.getLastLocation();
            if (loc != null) {
                params.put("gt", "1");
                params.put("lng", loc.getLongitude() + "");
                params.put("lat", loc.getLatitude() + "");
                params.put("gts", System.currentTimeMillis() / 1000 + "");
            }

            WifiManager wm = (WifiManager) ContextUtil.getContext().getSystemService(WIFI_SERVICE);
            WifiInfo connection = wm.getConnectionInfo();
            if (connection != null) {
                params.put("ap_mac", connection.getMacAddress());
                params.put("rssi", connection.getRssi() + "");
                params.put("is_connected", isWifiEnabled() + "");
                params.put("ap_name", formatSSID(connection.getSSID()));
            }


        } catch (Throwable e) {
            e.printStackTrace();
        }

        return params;

    }


    public static Map<String, String> getTrackApiCommonParams() {
        Map<String, String> params = new HashMap<>();

        try {
            params.put("os_version", Build.VERSION.RELEASE);
            params.put("app_package", ContextUtil.getContext().getPackageName());
            params.put("app_version", AppUtils.getAppVersionName());
            params.put("device_type", AppUtils.isTabletDevice() ? "2" : "1");
            params.put("vendor", urlEncode(Build.BRAND));
            params.put("model", urlEncode(Build.MODEL));

            if (TextUtils.isEmpty(imei)) {
                imei = PhoneUtils.getIMEI();
            }

            if (TextUtils.isEmpty(mac)) {
                mac = DeviceUtils.getMacAddress();
            }

            params.put("imei", imei);
            params.put("mac", mac);
            params.put("android_id", DeviceUtils.getAndroidID());
            params.put("screen_size", ScreenUtils.getScreenWidth() + "x" + ScreenUtils.getScreenHeight());
            params.put("os_type", "ANDROID");
            params.put("operator_type", getSimOperatorInfo() + "");
            params.put("connection_type", getNetworkType());

            Location loc = SystemLocationUtil.getLastLocation();
            if (loc != null) {
                params.put("CoordinateType", "1");
                params.put("longitude", loc.getLongitude() + "");
                params.put("latitude", loc.getLatitude() + "");
                params.put("timestamp", System.currentTimeMillis() / 1000 + "");
            }

            WifiManager wm = (WifiManager) ContextUtil.getContext().getSystemService(WIFI_SERVICE);
            WifiInfo connection = wm.getConnectionInfo();
            if (connection != null) {
                params.put("ap_mac", connection.getMacAddress());
                params.put("rssi", connection.getRssi() + "");
                params.put("is_connected", isWifiEnabled() + "");
                params.put("ap_name", formatSSID(connection.getSSID()));
            }


        } catch (Throwable e) {
            e.printStackTrace();
        }

        return params;

    }


    private static String formatSSID(String rawSSID) {
        try {
            if (TextUtils.isEmpty(rawSSID)) {
                return "";
            }
            rawSSID = rawSSID.trim();

            if (rawSSID.startsWith("\"") && rawSSID.endsWith("\"")) {
                rawSSID = rawSSID.substring(1, rawSSID.length() - 1);
            }

            return rawSSID;

        } catch (Throwable e) {
            e.printStackTrace();
            return "";
        }
    }


    public static String getTrackUrlCommonParams() {
        try {
            StringBuilder sb = new StringBuilder();
            Map<String, String> params = getTrackApiCommonParams();
            for (Map.Entry<String, String> entry : params.entrySet()) {
                sb.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
            }
            String ret = sb.toString();
            if (ret.endsWith("&")) {
                ret = ret.substring(0, ret.length() - 1);
            }

            return ret;

        } catch (Throwable e) {
            return "";
        }
    }


    public static Map<String, String> commonHeader() {
        Map<String, String> header = new HashMap<>();
        header.put("User-Agent", ApiUtil.getUserAgent());
        return header;
    }


    public static String getAdUrlCommonParams() {
        StringBuilder sb = new StringBuilder();
        Map<String, String> params = getAdApiCommonParams();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            sb.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
        }
        String ret = sb.toString();
        if (ret.endsWith("&")) {
            ret = ret.substring(0, ret.length() - 1);
        }

        return ret;
    }

    public static String urlEncode(String input) {
        try {
            return URLEncoder.encode(input, "utf-8");
        } catch (Throwable e) {
            e.printStackTrace();
            return "";
        }
    }

    private static boolean isWifiEnabled() {
        try {
            Context myContext = ContextUtil.getContext();
            WifiManager wifiMgr = (WifiManager) myContext.getSystemService(Context.WIFI_SERVICE);
            if (wifiMgr.isWifiEnabled()) {
                ConnectivityManager connManager = (ConnectivityManager) myContext
                        .getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo wifiInfo = connManager
                        .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
                return wifiInfo.isConnected();
            } else {
                return false;
            }

        } catch (Throwable e) {

        }

        return false;

    }


    private static int getSimOperatorInfo() {
        try {
            TelephonyManager telephonyManager = (TelephonyManager) ContextUtil.getContext().getSystemService(Context.TELEPHONY_SERVICE);
            String operatorString = telephonyManager.getSimOperator();

            if (operatorString == null) {
                return 0;
            }

            if (operatorString.equals("46000") || operatorString.equals("46002")) {
                //中国移动
                return 1;
            } else if (operatorString.equals("46001")) {
                //中国联通
                return 3;
            } else if (operatorString.equals("46003")) {
                //中国电信
                return 2;
            }

        } catch (Throwable e) {
        }

        return 99;

    }


    private static final String NETWORK_CLASS_UNKNOWN = "-1";
    private static final String NETWORK_CLASS_WIFI = "1";
    private static final String NETWORK_CLASS_2_G = "2";
    private static final String NETWORK_CLASS_3_G = "3";
    private static final String NETWORK_CLASS_4_G = "4";
    private static final String NETWORK_CLASS_5_G = "5";


    private static String getNetworkType() {
        try {

            ConnectivityManager manager = (ConnectivityManager) ContextUtil.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeInfo = manager.getActiveNetworkInfo();

            if (activeInfo == null) {
                return NETWORK_CLASS_UNKNOWN;
            } else if (activeInfo.isConnected()) {
                int networkType = activeInfo.getType();
                if (ConnectivityManager.TYPE_WIFI == networkType) {
                    return NETWORK_CLASS_WIFI;
                } else if (ConnectivityManager.TYPE_MOBILE == networkType) {
                    int subtype = activeInfo.getSubtype();
                    if (TelephonyManager.NETWORK_TYPE_GPRS == subtype
                            || TelephonyManager.NETWORK_TYPE_GSM == subtype
                            || TelephonyManager.NETWORK_TYPE_EDGE == subtype
                            || TelephonyManager.NETWORK_TYPE_CDMA == subtype
                            || TelephonyManager.NETWORK_TYPE_1xRTT == subtype
                            || TelephonyManager.NETWORK_TYPE_IDEN == subtype) {
                        return NETWORK_CLASS_2_G;
                    } else if (TelephonyManager.NETWORK_TYPE_UMTS == subtype
                            || TelephonyManager.NETWORK_TYPE_EVDO_0 == subtype
                            || TelephonyManager.NETWORK_TYPE_EVDO_A == subtype
                            || TelephonyManager.NETWORK_TYPE_HSDPA == subtype
                            || TelephonyManager.NETWORK_TYPE_HSUPA == subtype
                            || TelephonyManager.NETWORK_TYPE_HSPA == subtype
                            || TelephonyManager.NETWORK_TYPE_EVDO_B == subtype
                            || TelephonyManager.NETWORK_TYPE_EHRPD == subtype
                            || TelephonyManager.NETWORK_TYPE_HSPAP == subtype
                            || TelephonyManager.NETWORK_TYPE_TD_SCDMA == subtype) {
                        return NETWORK_CLASS_3_G;
                    } else if (TelephonyManager.NETWORK_TYPE_LTE == subtype
                            || TelephonyManager.NETWORK_TYPE_IWLAN == subtype) {
                        return NETWORK_CLASS_4_G;
                    } else {
                        return NETWORK_CLASS_UNKNOWN;
                    }
                } else {
                    return "999";
                }
            }

            return NETWORK_CLASS_UNKNOWN;

        } catch (Throwable e) {
            return NETWORK_CLASS_UNKNOWN;
        }
    }


    /**
     * 获取系统user agent
     */
    public static String getUserAgent() {
        if (!TextUtils.isEmpty(userAgent)) {
            return userAgent;
        }

        try {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                try {
                    userAgent = WebSettings.getDefaultUserAgent(ContextUtil.getContext());
                } catch (Exception e) {
                    userAgent = System.getProperty("http.agent");
                }
            } else {
                userAgent = System.getProperty("http.agent");
            }

            StringBuffer sb = new StringBuffer();
            for (int i = 0, length = userAgent.length(); i < length; i++) {
                char c = userAgent.charAt(i);
                if (c <= '\u001f' || c >= '\u007f') {
                    sb.append(String.format("\\u%04x", (int) c));
                } else {
                    sb.append(c);
                }
            }
            userAgent = sb.toString();

        } catch (Throwable e) {
            e.printStackTrace();
        }

        return userAgent == null ? "" : userAgent;
    }

}
