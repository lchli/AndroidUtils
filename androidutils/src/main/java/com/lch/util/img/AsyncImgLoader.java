package com.lch.util.img;

import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import com.lch.util.ContextUtil;
import com.lch.util.IOUtils;
import com.lch.util.cache.DiskLruCacheHelper;
import com.lch.util.http.SSLHelper;

import java.io.FileInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class AsyncImgLoader extends AsyncDataLoader<Bitmap, ImgLoadParam> {
    private static final int MAX_CACHE_SIZE = 100 * 1024 * 1024;
    private static final String CACHE_DIR_NAME = "srsc-ad-sdk-cache";
    private static DiskLruCacheHelper cacheHelper;


    @Override
    protected Bitmap loadImpl(ImgLoadParam param) {
        try {
            if (param.model instanceof String) {//path
                if (((String) param.model).startsWith("http://") || ((String) param.model).startsWith("https://")) {
                    return loadURL(param);
                } else {
                    return loadPath(param);
                }
            }

            if (param.model instanceof Uri) {//
                return loadUri(param);
            }

            if (param.model instanceof Integer) {//
                return loadResID(param);
            }

        } catch (Throwable e) {
            e.printStackTrace();
        }

        return null;
    }


    private Bitmap loadResID(ImgLoadParam param) {
        try {

            if (param.resizeW > 0 && param.resizeH > 0) {
                return getBitmap((int) param.model, param.resizeW, param.resizeH);
            }

            return BitmapFactory.decodeResource(ContextUtil.getContext().getResources(), (int) param.model);
        } catch (Throwable e) {
            e.printStackTrace();
            return null;
        } finally {

        }
    }

    private Bitmap loadUri(ImgLoadParam param) {
        InputStream ins = null;
        try {
            ContentResolver aResolver = ContextUtil.getContext().getContentResolver();
            ins = aResolver.openInputStream((Uri) param.model);
            byte[] bytes = IOUtils.toByteArray(ins);

            if (param.resizeW > 0 && param.resizeH > 0) {
                return getBitmap(bytes, 0, param.resizeW, param.resizeH);
            }

            return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        } catch (Throwable e) {
            e.printStackTrace();
            return null;
        } finally {
            IOUtils.closeQuietly(ins);
        }

    }

    private Bitmap loadPath(ImgLoadParam param) {
        InputStream ins = null;
        try {
            ins = new FileInputStream((String) param.model);
            byte[] bytes = IOUtils.toByteArray(ins);

            if (param.resizeW > 0 && param.resizeH > 0) {
                return getBitmap(bytes, 0, param.resizeW, param.resizeH);
            }

            return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        } catch (Throwable e) {
            e.printStackTrace();
            return null;
        } finally {
            IOUtils.closeQuietly(ins);
        }

    }

    @Override
    protected Bitmap getCache(ImgLoadParam param) {
        return getCache(param.model + "");
    }

    @Override
    protected void putCache(ImgLoadParam param, Bitmap res) {
        putCache(param.model + "", res);
    }

    private static Bitmap getCache(String key) {
        try {
            if (cacheHelper == null) {
                cacheHelper = new DiskLruCacheHelper(ContextUtil.getContext(), CACHE_DIR_NAME, MAX_CACHE_SIZE);
            }

            return cacheHelper.getAsBitmap(key);

        } catch (Throwable e) {
            e.printStackTrace();
            return null;
        }

    }


    private static void putCache(String key, Bitmap bmp) {
        try {
            if (cacheHelper == null) {
                cacheHelper = new DiskLruCacheHelper(ContextUtil.getContext(), CACHE_DIR_NAME, MAX_CACHE_SIZE);
            }

            cacheHelper.put(key, bmp);

        } catch (Throwable e) {
            e.printStackTrace();
        }

    }


    private Bitmap loadURL(ImgLoadParam param) {
        InputStream ins = null;
        try {
            URL url = new URL((String) param.model);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            if (connection instanceof HttpsURLConnection) {
                SSLHelper.configSSL((HttpsURLConnection) connection);
            }

            connection.setRequestMethod("GET");
            connection.setDoInput(true);
            connection.connect();

            ins = connection.getInputStream();
            byte[] bytes = IOUtils.toByteArray(ins);

            if (param.resizeW > 0 && param.resizeH > 0) {
                return getBitmap(bytes, 0, param.resizeW, param.resizeH);
            }

            return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        } catch (Throwable e) {
            e.printStackTrace();
            return null;
        } finally {
            IOUtils.closeQuietly(ins);
        }

    }

    private static Bitmap getBitmap(final byte[] data,
                                    final int offset,
                                    final int maxWidth,
                                    final int maxHeight) {
        if (data.length == 0) return null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(data, offset, data.length, options);
        options.inSampleSize = calculateInSampleSize(options, maxWidth, maxHeight);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeByteArray(data, offset, data.length, options);
    }

    private static Bitmap getBitmap(final int resID,
                                    final int maxWidth,
                                    final int maxHeight) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(ContextUtil.getContext().getResources(), resID, options);
        options.inSampleSize = calculateInSampleSize(options, maxWidth, maxHeight);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(ContextUtil.getContext().getResources(), resID, options);
    }

    private static int calculateInSampleSize(final BitmapFactory.Options options,
                                             final int maxWidth,
                                             final int maxHeight) {
        int height = options.outHeight;
        int width = options.outWidth;
        int inSampleSize = 1;
        while (height > maxHeight || width > maxWidth) {
            height >>= 1;
            width >>= 1;
            inSampleSize <<= 1;
        }
        return inSampleSize;
    }
}
