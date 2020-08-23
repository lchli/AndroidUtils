package com.lch.util.http;

import android.net.Uri;
import android.support.annotation.NonNull;

import com.lch.util.IOUtils;
import com.lch.util.executor.ResultDto;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

public final class HttpUtils {
    private static final int TIME_OUT = 30_000;
    private static final String UTF8 = "UTF-8";

    @NonNull
    public static ResultDto<String> get(String urlPath, Map<String, String> headers, Map<String, String> params) {
        ResultDto<String> result = new ResultDto<>();
        InputStream ins = null;

        try {

            if (params != null) {
                urlPath = HttpRequestTool.addParamToUrl(urlPath, params);
            }

            URL url = new URL(urlPath);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            if (conn instanceof HttpsURLConnection) {
                SSLHelper.configSSL((HttpsURLConnection) conn);
            }
            conn.setReadTimeout(TIME_OUT);
            conn.setConnectTimeout(TIME_OUT);
            conn.setRequestMethod("GET");
            conn.setUseCaches(false);
            conn.setDoInput(true);

            if (headers != null) {
                for (Map.Entry<String, String> entry : headers.entrySet()) {
                    conn.setRequestProperty(entry.getKey(), entry.getValue());
                }
            }

            conn.connect();

            ins = conn.getInputStream();
            String body = IOUtils.toString(ins);

            result.setCode(conn.getResponseCode());
            result.setData(body);

        } catch (Throwable e) {
            e.printStackTrace();
            result.setMsg(e.getMessage());
        } finally {
            IOUtils.closeQuietly(ins);
        }

        return result;
    }


    @NonNull
    public static ResultDto<File> getFile(String urlPath, Map<String, String> headers, Map<String, String> params, File save) {
        ResultDto<File> result = new ResultDto<>();
        InputStream ins = null;
        OutputStream outputStream = null;

        try {
            if (params != null) {
                urlPath = HttpRequestTool.addParamToUrl(urlPath, params);
            }

            URL url = new URL(urlPath);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            if (conn instanceof HttpsURLConnection) {
                SSLHelper.configSSL((HttpsURLConnection) conn);
            }
            conn.setReadTimeout(TIME_OUT);
            conn.setConnectTimeout(TIME_OUT);
            conn.setRequestMethod("GET");
            conn.setUseCaches(false);
            conn.setDoInput(true);

            if (headers != null) {
                for (Map.Entry<String, String> entry : headers.entrySet()) {
                    conn.setRequestProperty(entry.getKey(), entry.getValue());
                }
            }

            conn.connect();

            ins = conn.getInputStream();
            outputStream = new FileOutputStream(save);
            IOUtils.write(ins, outputStream);

            result.setCode(conn.getResponseCode());
            result.setData(save);

        } catch (Throwable e) {
            e.printStackTrace();
            result.setMsg(e.getMessage());
        } finally {
            IOUtils.closeQuietly(ins);
            IOUtils.closeQuietly(outputStream);
        }

        return result;
    }


    @NonNull
    public static ResultDto<String> postForm(String urlPath, Map<String, String> headers, Map<String, String> params, int timeoutMills) {
        ResultDto<String> result = new ResultDto<>();
        InputStream ins = null;
        OutputStream os = null;

        try {

            URL url = new URL(urlPath);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            if (conn instanceof HttpsURLConnection) {
                SSLHelper.configSSL((HttpsURLConnection) conn);
            }
            conn.setReadTimeout(timeoutMills);
            conn.setConnectTimeout(timeoutMills);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded");
            conn.setUseCaches(false);
            conn.setDoOutput(true);
            conn.setDoInput(true);

            if (headers != null) {
                for (Map.Entry<String, String> entry : headers.entrySet()) {
                    conn.setRequestProperty(entry.getKey(), entry.getValue());
                }
            }

            Uri.Builder builder = new Uri.Builder();
            if (params != null) {
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    builder.appendQueryParameter(entry.getKey(), entry.getValue());
                }
            }
            String query = builder.build().getEncodedQuery();
            if (query == null) {
                query = "";
            }

            os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, UTF8));
            writer.write(query);
            writer.flush();

            ins = conn.getInputStream();
            String body = IOUtils.toString(ins);

            result.setCode(conn.getResponseCode());
            result.setData(body);

        } catch (Throwable e) {
            e.printStackTrace();
            result.setMsg(e.getMessage());
        } finally {
            IOUtils.closeQuietly(ins);
            IOUtils.closeQuietly(os);
        }

        return result;
    }


    @NonNull
    public static ResultDto<String> postForm(String urlPath, Map<String, String> headers, Map<String, String> params) {
        return postForm(urlPath, headers, params, TIME_OUT);
    }


    @NonNull
    public static ResultDto<String> postJson(String urlPath, Map<String, String> headers, String params) {
        ResultDto<String> result = new ResultDto<>();
        InputStream ins = null;
        OutputStream os = null;

        try {

            URL url = new URL(urlPath);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            if (conn instanceof HttpsURLConnection) {
                SSLHelper.configSSL((HttpsURLConnection) conn);
            }
            conn.setReadTimeout(TIME_OUT);
            conn.setConnectTimeout(TIME_OUT);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json; utf-8");
            conn.setUseCaches(false);
            conn.setDoOutput(true);
            conn.setDoInput(true);

            if (headers != null) {
                for (Map.Entry<String, String> entry : headers.entrySet()) {
                    conn.setRequestProperty(entry.getKey(), entry.getValue());
                }
            }

            os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, UTF8));
            writer.write(params);
            writer.flush();

            ins = conn.getInputStream();
            String body = IOUtils.toString(ins);

            result.setCode(conn.getResponseCode());
            result.setData(body);

        } catch (Throwable e) {
            e.printStackTrace();
            result.setMsg(e.getMessage());
        } finally {
            IOUtils.closeQuietly(ins);
            IOUtils.closeQuietly(os);
        }

        return result;
    }

}
