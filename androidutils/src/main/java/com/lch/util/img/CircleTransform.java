package com.lch.util.img;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;

public class CircleTransform implements BitmapTransform {

    private final Paint borderPaint = new Paint();
    private final Paint paint = new Paint();

    {
        paint.setAntiAlias(true);

        borderPaint.setAntiAlias(true);
        borderPaint.setDither(true);
        borderPaint.setStyle(Paint.Style.STROKE);
    }

    private int borderColor;
    private float borderWidth;

    public CircleTransform(float borderWidth, int borderColor) {
        this.borderWidth = borderWidth;
        this.borderColor = borderColor;
        borderPaint.setStrokeWidth(borderWidth);
        borderPaint.setColor(borderColor);
    }


    @Override
    public Bitmap transform(Bitmap toTransform) {
        if (toTransform == null) {
            return null;
        }

        int width = toTransform.getWidth();
        int height = toTransform.getHeight();

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        bitmap.setHasAlpha(true);

        float r = Math.min(width, height) / 2f;
        float cx = width / 2f;
        float cy = height / 2f;

        Path path = new Path();
        path.addCircle(cx, cy, r, Path.Direction.CW);

        Canvas canvas = new Canvas(bitmap);
        canvas.drawPath(path, paint);

        RectF rectFCircle = new RectF(cx - r, cy - r, cx + r, cy + r);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));

        canvas.drawBitmap(toTransform, null, rectFCircle, paint);

        if (borderWidth > 0) {
            canvas.clipPath(path);
            canvas.drawPath(path, borderPaint);
        }

        toTransform.recycle();

        return bitmap;
    }
}
