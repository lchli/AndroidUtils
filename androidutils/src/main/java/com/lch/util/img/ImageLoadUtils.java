package com.lch.util.img;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.widget.ImageView;


import com.lch.util.ContextUtil;
import com.lch.util.EncryptUtils;
import com.lch.util.IOUtils;
import com.lch.util.cache.DiskLruCacheHelper;
import com.lch.util.executor.BgTask;
import com.lch.util.http.SSLHelper;

import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public final class ImageLoadUtils {
    private static final int MAX_CACHE_SIZE = 100 * 1024 * 1024;
    private static final String CACHE_DIR_NAME = "srsc-ad-sdk-cache";
    private static DiskLruCacheHelper cacheHelper;

    public static void load(final String url, ImageView imageView, final BitmapTransform transform) {//to do disk cache.
        load(url, imageView, 0, 0, transform);
    }


    public static void load(final String url, ImageView imageView) {
        load(url, imageView, 0, 0, null);
    }


    public static void load(final String url, ImageView imageView, final int resizeW, final int resizeH) {
        load(url, imageView, resizeW, resizeH, null);
    }


    public static void load(final String url, ImageView imageView, final int resizeW, final int resizeH, final BitmapTransform transform) {
        try {

            if (TextUtils.isEmpty(url) || imageView == null) {
                return;
            }
            imageView.setTag(url);
            final WeakReference<ImageView> ref = new WeakReference<>(imageView);

            new BgTask<Bitmap>() {
                @Override
                protected Bitmap doInBackground() {
                    return getOrDownloadBitmap(url, resizeW, resizeH, transform);
                }

                @Override
                protected void doPost(Bitmap result) {
                    if (result == null) {
                        return;
                    }
                    ImageView iv = ref.get();
                    if (iv == null) {
                        return;
                    }
                    String tagurl = (String) iv.getTag();
                    if (!url.equals(tagurl)) {
                        return;
                    }
                    iv.setImageBitmap(result);

                }
            }.execute();

        } catch (Throwable e) {
            e.printStackTrace();
        }


    }


    private static Bitmap getOrDownloadBitmap(String url, final int resizeW, final int resizeH, final BitmapTransform transformer) {
        try {

            final String key = EncryptUtils.encryptMD5ToString(url);

            Bitmap cache = getCache(key);
            if (cache != null) {
                return transform(cache, transformer);
            }

            Bitmap bmp = dowloadImage(url, resizeW, resizeH);
            if (bmp != null) {
                putCache(key, bmp);
            }

            return transform(bmp, transformer);

        } catch (Throwable e) {
            e.printStackTrace();
            return null;
        }
    }

    private static Bitmap transform(Bitmap source, BitmapTransform transformer) {
        if (transformer == null) {
            return source;
        }

        return transformer.transform(source);
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


    private static Bitmap dowloadImage(String urlpath, final int resizeW, final int resizeH) {
        InputStream ins = null;
        try {
            URL url = new URL(urlpath);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            if (connection instanceof HttpsURLConnection) {
                SSLHelper.configSSL((HttpsURLConnection) connection);
            }

            connection.setRequestMethod("GET");
            connection.setDoInput(true);
            connection.connect();

            ins = connection.getInputStream();
            byte[] bytes = IOUtils.toByteArray(ins);

            if (resizeW > 0 && resizeH > 0) {
                return getBitmap(bytes, 0, resizeW, resizeH);
            }

            return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        } catch (Throwable e) {
            e.printStackTrace();
            return null;
        } finally {
            IOUtils.closeQuietly(ins);
        }

    }


    /**
     * Return bitmap.
     *
     * @param data      The data.
     * @param offset    The offset.
     * @param maxWidth  The maximum width.
     * @param maxHeight The maximum height.
     * @return bitmap
     */
    public static Bitmap getBitmap(final byte[] data,
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

    /**
     * Return the sample size.
     *
     * @param options   The options.
     * @param maxWidth  The maximum width.
     * @param maxHeight The maximum height.
     * @return the sample size
     */
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
