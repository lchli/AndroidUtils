package com.lch.util.http;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.MimeTypeMap;

import com.lch.util.IOTools;
import com.lch.util.IOUtils;
import com.lch.util.executor.ResultDto;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.net.ssl.HttpsURLConnection;

public final class HttpUtils {
    private static final int TIME_OUT = 30_000;
    private static final String UTF8 = "UTF-8";
    private static final String M_CHARSET = UTF8;
    private static final String BOUNDARY = createBoundary();
    public static final String VALUE_APPLICATION_STREAM = "application/octet-stream";
    public static final String CONTENT_TYPE_MULTI_FORM = "multipart/form-data; boundary=" + BOUNDARY;


    @NonNull
    public static ResultDto<String> get(String urlPath, Map<String, String> headers, Map<String, Object> params) {
        ResultDto<String> result = new ResultDto<>();
        InputStream ins = null;

        try {

            if (params != null) {
                urlPath = HttpRequestTool.addParamToUrl(urlPath, params);
            }

            HttpURLConnection conn = createConnection(urlPath, "GET", null, TIME_OUT);
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
    public static ResultDto<File> getFile(String urlPath, Map<String, String> headers, Map<String, Object> params, File save) {
        ResultDto<File> result = new ResultDto<>();
        InputStream ins = null;
        OutputStream outputStream = null;

        try {
            if (params != null) {
                urlPath = HttpRequestTool.addParamToUrl(urlPath, params);
            }

            HttpURLConnection conn = createConnection(urlPath, "GET", null, TIME_OUT);
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
    public static ResultDto<String> postForm(String urlPath, Map<String, String> headers, Map<String, Object> params, int timeoutMills) {
        ResultDto<String> result = new ResultDto<>();
        InputStream ins = null;
        OutputStream os = null;

        try {

            HttpURLConnection conn = createConnection(urlPath, "POST", "application/x-www-form-urlencoded", timeoutMills);
            conn.setDoOutput(true);
            conn.setDoInput(true);

            if (headers != null) {
                for (Map.Entry<String, String> entry : headers.entrySet()) {
                    conn.setRequestProperty(entry.getKey(), entry.getValue());
                }
            }

            Uri.Builder builder = new Uri.Builder();
            if (params != null) {
                for (Map.Entry<String, Object> entry : params.entrySet()) {
                    builder.appendQueryParameter(entry.getKey(), entry.getValue() != null ? entry.getValue().toString() : "");
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
    public static ResultDto<String> postMultiPart(String urlPath, Map<String, String> headers, Map<String, Object> params) {
        return postMultiPart(urlPath, headers, params, TIME_OUT);
    }

    @NonNull
    public static ResultDto<String> postMultiPart(String urlPath, Map<String, String> headers, Map<String, Object> params, int timeoutMills) {
        ResultDto<String> result = new ResultDto<>();
        InputStream ins = null;
        OutputStream os = null;

        try {

            HttpURLConnection conn = createConnection(urlPath, "POST", CONTENT_TYPE_MULTI_FORM, timeoutMills);
            conn.setChunkedStreamingMode(256 * 1024);
            conn.setDoOutput(true);
            conn.setDoInput(true);

            if (headers != null) {
                for (Map.Entry<String, String> entry : headers.entrySet()) {
                    conn.setRequestProperty(entry.getKey(), entry.getValue());
                }
            }

            os = conn.getOutputStream();

            if (params != null) {
                Set<String> filesKey = params.keySet();
                for (String key : filesKey) {
                    Object value = params.get(key);

                    if (value instanceof String) {
                        writeFormString(os, key, (String) value);
                    } else if (value instanceof File) {
                        writeFormFile(os, key, (File) value);
                    } else if (value instanceof byte[]) {
                        writeFormFileBytes(os, key, (byte[]) value);
                    }
                }
            }

            IOTools.write(os, "\r\n", M_CHARSET);
            IOTools.write(os, "--" + BOUNDARY + "--\r\n", M_CHARSET);

            os.flush();

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
    public static ResultDto<String> postForm(String urlPath, Map<String, String> headers, Map<String, Object> params) {
        return postForm(urlPath, headers, params, TIME_OUT);
    }


    @NonNull
    public static ResultDto<String> postJson(String urlPath, Map<String, String> headers, String params) {
        ResultDto<String> result = new ResultDto<>();
        InputStream ins = null;
        OutputStream os = null;

        try {

            HttpURLConnection conn = createConnection(urlPath, "POST", "application/json; utf-8", TIME_OUT);
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

    private static HttpURLConnection createConnection(String urlPath, String method, String contentType, int timeoutMills) throws Exception {
        URL url = new URL(urlPath);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        if (conn instanceof HttpsURLConnection) {
            SSLHelper.configSSL((HttpsURLConnection) conn);
        }
        conn.setReadTimeout(timeoutMills);
        conn.setConnectTimeout(timeoutMills);
        conn.setInstanceFollowRedirects(false);
        conn.setRequestMethod(method);
        if (contentType != null) {
            conn.setRequestProperty("Content-Type", contentType);
        }
        conn.setUseCaches(false);

        return conn;
    }


    private static void writeFormString(OutputStream writer, String key, String value) throws IOException {
        IOTools.write(writer, "--" + BOUNDARY + "\r\n", M_CHARSET);
        IOTools.write(writer, "Content-Disposition: form-data; name=\"" + key + "\"", M_CHARSET);
        IOTools.write(writer, "\r\n\r\n", M_CHARSET);
        IOTools.write(writer, value, M_CHARSET);
        IOTools.write(writer, "\r\n", M_CHARSET);
    }

    private static void writeFormFile(OutputStream writer, String key, File value) throws IOException {
        if (value == null) {
            return;
        }
        IOTools.write(writer, "--" + BOUNDARY + "\r\n", M_CHARSET);
        IOTools.write(writer, "Content-Disposition: form-data; name=\"" + key + "\"", M_CHARSET);
        IOTools.write(writer, "; filename=\"" + value.getName() + "\"", M_CHARSET);
        IOTools.write(writer, "\r\n", M_CHARSET);
        IOTools.write(writer, "Content-Type: " + contentType(value) + "\r\n\r\n", M_CHARSET);
        if (writer instanceof LengthOutputStream) {
            ((LengthOutputStream) writer).write(value.length());
        } else {
            writeFile(writer, value);
        }

        IOTools.write(writer, "\r\n", M_CHARSET);
    }

    private static void writeFormFileBytes(OutputStream writer, String key, byte[] value) throws IOException {
        if (value == null) {
            return;
        }
        String filename=UUID.randomUUID().toString().replaceAll("-","");

        IOTools.write(writer, "--" + BOUNDARY + "\r\n", M_CHARSET);
        IOTools.write(writer, "Content-Disposition: form-data; name=\"" + key + "\"", M_CHARSET);
        IOTools.write(writer, "; filename=\"" + filename + "\"", M_CHARSET);
        IOTools.write(writer, "\r\n", M_CHARSET);
        IOTools.write(writer, "Content-Type: " + VALUE_APPLICATION_STREAM + "\r\n\r\n", M_CHARSET);
        if (writer instanceof LengthOutputStream) {
            ((LengthOutputStream) writer).write(value.length);
        } else {
            IOTools.write(writer, value);
        }

        IOTools.write(writer, "\r\n", M_CHARSET);
    }

    private static void writeFile(OutputStream writer, File file) throws IOException {
        InputStream stream = new FileInputStream(file);
        IOUtils.write(stream, writer);
        IOUtils.closeQuietly(stream);
    }

    public static String contentType(File file) {
        String fileName = file.getName();
        String extension = MimeTypeMap.getFileExtensionFromUrl(fileName);
        String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);

        if (TextUtils.isEmpty(mimeType)) mimeType = VALUE_APPLICATION_STREAM;

        return mimeType;
    }

    private static String createBoundary() {
        StringBuilder sb = new StringBuilder("-------FormBoundary");
        for (int t = 1; t < 12; t++) {
            long time = System.currentTimeMillis() + t;
            if (time % 3L == 0L) {
                sb.append((char) (int) time % '\t');
            } else if (time % 3L == 1L) {
                sb.append((char) (int) (65L + time % 26L));
            } else {
                sb.append((char) (int) (97L + time % 26L));
            }
        }
        return sb.toString();
    }

}
