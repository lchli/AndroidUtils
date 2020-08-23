package com.lch.util.img;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.NonNull;

public class BlurTransform implements BitmapTransform {
    private static final int MAX_RADIUS = 25;
    private static final int DEFAULT_DOWN_SAMPLING = 1;

    @Override
    public Bitmap transform(Bitmap source) {
        if (source == null) {
            return null;
        }
        Bitmap newBmp = blurTransform(source);
        source.recycle();
        return newBmp;
    }


    public static Bitmap blurTransform(int radius, int sampling,
                                       @NonNull Bitmap toTransform) {

        int width = toTransform.getWidth();
        int height = toTransform.getHeight();
        int scaledWidth = width / sampling;
        int scaledHeight = height / sampling;

        Bitmap bitmap = Bitmap.createBitmap(scaledWidth, scaledHeight, Bitmap.Config.ARGB_8888);

        setCanvasBitmapDensity(toTransform, bitmap);

        Canvas canvas = new Canvas(bitmap);
        canvas.scale(1 / (float) sampling, 1 / (float) sampling);
        Paint paint = new Paint();
        paint.setFlags(Paint.FILTER_BITMAP_FLAG);
        canvas.drawBitmap(toTransform, 0, 0, paint);

        bitmap = FastBlur.blur(bitmap, radius, true);

        return bitmap;
    }

    public static Bitmap blurTransform(@NonNull Bitmap toTransform) {
        return blurTransform(MAX_RADIUS, DEFAULT_DOWN_SAMPLING, toTransform);
    }


    private static void setCanvasBitmapDensity(@NonNull Bitmap toTransform, @NonNull Bitmap canvasBitmap) {
        canvasBitmap.setDensity(toTransform.getDensity());
    }
}
