package com.lch.util.http;

import android.support.annotation.NonNull;

import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public final class HttpRequestTool {
    public HttpRequestTool() {
    }

    public static String addParamToUrl(@NonNull String url, @NonNull Map<String, Object> params) {

        if (url.contains("?")) {
            if (!url.endsWith("&") && !url.endsWith("?")) {
                url = url + "&";
            }

        } else {
            url = url + "?";
        }

        StringBuilder sb = new StringBuilder(url);
        Iterator var3 = params.entrySet().iterator();

        while (var3.hasNext()) {
            Entry<String, String> entry = (Entry) var3.next();
            sb.append((String) entry.getKey()).append("=").append(urlEncode((String) entry.getValue())).append("&");
        }

        String res = sb.toString();

        if (res.endsWith("&")) {
            res = res.substring(0, res.length() - 1);
        }

        return res;
    }

    public static String addParamToUrl(@NonNull String url, @NonNull JSONObject params) {
        if (url.contains("?")) {
            if (!url.endsWith("&") && !url.endsWith("?")) {
                url = url + "&";
            }

        } else {
            url = url + "?";
        }


        StringBuilder sb = new StringBuilder(url);
        Iterator keys = params.keys();

        while (keys.hasNext()) {
            String key = (String) keys.next();
            sb.append(key).append("=").append(urlEncode(params.optString(key))).append("&");
        }

        String res = sb.toString();

        if (res.endsWith("&")) {
            res = res.substring(0, res.length() - 1);
        }

        return res;
    }

    public static String urlEncode(String s) {
        try {
            return URLEncoder.encode(s, "utf-8");
        } catch (Throwable var2) {
            var2.printStackTrace();
            return s;
        }
    }
}